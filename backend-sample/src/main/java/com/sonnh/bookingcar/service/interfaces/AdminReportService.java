package com.sonnh.bookingcar.service.interfaces;

import com.sonnh.bookingcar.dto.response.admin.RevenueReportResDto;
import java.time.LocalDate;

public interface AdminReportService {
    RevenueReportResDto getRevenueReport(LocalDate startDate, LocalDate endDate, String groupBy);
}
