package com.sonnh.bookingcar.controller.admin;

import com.sonnh.bookingcar.dto.response.admin.RevenueReportResDto;
import com.sonnh.bookingcar.service.interfaces.AdminReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@Tag(name = "Reports & Analytics")
public class AdminReportController {

    private final AdminReportService adminReportService;

    @GetMapping("/revenue")
    public ResponseEntity<RevenueReportResDto> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "DAY") String groupBy) {
        return ResponseEntity.ok(adminReportService.getRevenueReport(startDate, endDate, groupBy));
    }
}
