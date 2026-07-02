package com.sonnh.bookingcar.controller.admin;

import com.sonnh.bookingcar.dto.request.admin.ShiftCreateReqDto;
import com.sonnh.bookingcar.dto.request.admin.ShiftUpdateReqDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.admin.ShiftResDto;
import com.sonnh.bookingcar.service.interfaces.ShiftService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/shifts")
@RequiredArgsConstructor
@Tag(name = "Manage Shifts")
public class AdminShiftController {

    private final ShiftService shiftService;

    @GetMapping
    public ResponseEntity<PageImplResDto<ShiftResDto>> getAllShifts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return ResponseEntity.ok(shiftService.getAllShifts(page, size));
    }

    @GetMapping("/{shiftId}")
    public ResponseEntity<ShiftResDto> getShiftById(@PathVariable UUID shiftId) {
        return ResponseEntity.ok(shiftService.getShiftById(shiftId));
    }

    @PostMapping
    public ResponseEntity<ShiftResDto> createShift(@RequestBody ShiftCreateReqDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shiftService.createShift(dto));
    }

    @PutMapping("/{shiftId}")
    public ResponseEntity<ShiftResDto> updateShift(@PathVariable UUID shiftId, @RequestBody ShiftUpdateReqDto dto) {
        return ResponseEntity.ok(shiftService.updateShift(shiftId, dto));
    }

    @PatchMapping("/{shiftId}/request-early-closure")
    public ResponseEntity<Void> requestEarlyClosure(@PathVariable UUID shiftId) {
        shiftService.requestEarlyClosure(shiftId);
        return ResponseEntity.ok().build();
    }
}

