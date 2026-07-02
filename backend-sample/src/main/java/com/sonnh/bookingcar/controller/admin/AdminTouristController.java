package com.sonnh.bookingcar.controller.admin;

import com.sonnh.bookingcar.dto.UserTouristDto;
import com.sonnh.bookingcar.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/tourists")
@RequiredArgsConstructor
@Tag(name = "Manage Tourist")
public class AdminTouristController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserTouristDto>> findAll() {
        return ResponseEntity.ok(userService.findAllTourists());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserTouristDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findTouristById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.deleteTourist(id);
        return ResponseEntity.noContent().build();
    }
}
