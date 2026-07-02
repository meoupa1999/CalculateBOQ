package com.sonnh.bookingcar.service.impl;

import com.sonnh.bookingcar.data.domain.ServiceRequest;
import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import com.sonnh.bookingcar.data.domain.enums.ServiceType;
import com.sonnh.bookingcar.data.repository.ServiceRequestRepository;
import com.sonnh.bookingcar.dto.response.admin.RevenueDataPointDto;
import com.sonnh.bookingcar.dto.response.admin.RevenueReportResDto;
import com.sonnh.bookingcar.service.interfaces.AdminReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminReportServiceImpl implements AdminReportService {

    private final ServiceRequestRepository serviceRequestRepository;

    @Override
    public RevenueReportResDto getRevenueReport(LocalDate startDate, LocalDate endDate, String groupBy) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<ServiceRequest> requests = serviceRequestRepository.findByStatusAndAuditCreatedAtBetween(
                BookingStatus.DONE, start, end);

        BigDecimal totalRevenue = requests.stream()
                .map(sr -> sr.getTotalPrice() != null ? sr.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Group by Period
        Map<String, RevenueDataPointDto> periodMap = new TreeMap<>();
        DateTimeFormatter formatter = groupBy.equalsIgnoreCase("MONTH") 
                ? DateTimeFormatter.ofPattern("MM/yyyy") 
                : DateTimeFormatter.ofPattern("dd/MM");

        requests.forEach(sr -> {
            String label = sr.getAudit().getCreatedAt().format(formatter);
            RevenueDataPointDto point = periodMap.getOrDefault(label, 
                    RevenueDataPointDto.builder().label(label).amount(BigDecimal.ZERO).count(0L).build());
            
            BigDecimal price = sr.getTotalPrice() != null ? sr.getTotalPrice() : BigDecimal.ZERO;
            point.setAmount(point.getAmount().add(price));
            point.setCount(point.getCount() + 1);
            periodMap.put(label, point);
        });

        // Group by Service Type
        Map<ServiceType, RevenueDataPointDto> serviceMap = new HashMap<>();
        requests.forEach(sr -> {
            ServiceType type = sr.getType();
            RevenueDataPointDto point = serviceMap.getOrDefault(type, 
                    RevenueDataPointDto.builder().label(type.name()).amount(BigDecimal.ZERO).count(0L).build());
            
            BigDecimal price = sr.getTotalPrice() != null ? sr.getTotalPrice() : BigDecimal.ZERO;
            point.setAmount(point.getAmount().add(price));
            point.setCount(point.getCount() + 1);
            serviceMap.put(type, point);
        });

        return RevenueReportResDto.builder()
                .totalRevenue(totalRevenue)
                .totalBookings((long) requests.size())
                .revenueByPeriod(new ArrayList<>(periodMap.values()))
                .revenueByService(serviceMap.entrySet().stream()
                        .map(e -> {
                            String label = e.getKey() == ServiceType.TOUR ? "Tour Du Lịch" : "Sân Bay / Thuê Xe";
                            e.getValue().setLabel(label);
                            return e.getValue();
                        })
                        .collect(Collectors.toList()))
                .revenueByDriver(getRevenueByDriver(requests))
                .build();
    }

    private List<RevenueDataPointDto> getRevenueByDriver(List<ServiceRequest> requests) {
        Map<String, RevenueDataPointDto> driverMap = new HashMap<>();

        for (ServiceRequest sr : requests) {
            String driverName = "Chưa chỉ định";
            if (sr.getType() == ServiceType.TOUR && sr.getTourBookingDetail() != null 
                    && sr.getTourBookingDetail().getCurrentShift() != null) {
                driverName = sr.getTourBookingDetail().getCurrentShift().getDriver().getFullName();
            } else if (sr.getType() == ServiceType.AIRPORT && sr.getAirportTransferDetail() != null
                    && sr.getAirportTransferDetail().getCurrentShift() != null) {
                driverName = sr.getAirportTransferDetail().getCurrentShift().getDriver().getFullName();
            }

            RevenueDataPointDto point = driverMap.getOrDefault(driverName,
                    RevenueDataPointDto.builder().label(driverName).amount(BigDecimal.ZERO).count(0L).build());

            BigDecimal price = sr.getTotalPrice() != null ? sr.getTotalPrice() : BigDecimal.ZERO;
            point.setAmount(point.getAmount().add(price));
            point.setCount(point.getCount() + 1);
            driverMap.put(driverName, point);
        }

        return driverMap.values().stream()
                .sorted((a, b) -> b.getAmount().compareTo(a.getAmount()))
                .limit(10) // Top 10 drivers
                .collect(Collectors.toList());
    }
}
