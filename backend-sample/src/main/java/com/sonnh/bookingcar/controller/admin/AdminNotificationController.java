package com.sonnh.bookingcar.controller.admin;

import com.sonnh.bookingcar.service.interfaces.NotificationsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
@Tag(name = "Manage Notifications")
public class AdminNotificationController {

    private final NotificationsService notificationsService;

    @PutMapping("/read/{id}")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id) {
        notificationsService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }
}
