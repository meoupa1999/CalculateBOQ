package com.sonnh.bookingcar.controller.admin;

import com.sonnh.bookingcar.dto.response.admin.CheckinTourHistoryResDto;
import com.sonnh.bookingcar.service.interfaces.CheckinTourHistoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/checkin-histories")
@RequiredArgsConstructor
@Tag(name = "Checkin History Admin")
public class AdminCheckinHistoryController {

    private final CheckinTourHistoryService service;

    @GetMapping
    public ResponseEntity<List<CheckinTourHistoryResDto>> getAll(@org.springframework.web.bind.annotation.RequestParam(required = false) UUID tourId) {
        return ResponseEntity.ok(service.getAll(tourId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheckinTourHistoryResDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }
}
