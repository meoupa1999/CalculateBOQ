package com.sonnh.bookingcar.controller.admin;

import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.admin.DriverResDto;
import com.sonnh.bookingcar.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/driver-registrations")
@RequiredArgsConstructor
@Tag(name = "Manage Driver Register")
public class AdminDriverRegistrationController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tài xế đăng ký (Pending/Rejected)")
    public ResponseEntity<PageImplResDto<DriverResDto>> getDriverRegistrations(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(userService.getDriverRegistrations(page, size, search));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Duyệt hồ sơ tài xế")
    public ResponseEntity<Void> approveDriver(@PathVariable UUID id) {
        userService.approveDriver(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Từ chối hồ sơ tài xế")
    public ResponseEntity<Void> rejectDriver(@PathVariable UUID id, @RequestParam(required = false) String reason) {
        userService.rejectDriver(id, reason);
        return ResponseEntity.ok().build();
    }
}
