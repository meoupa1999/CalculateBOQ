package com.sonnh.elv.controller;

import com.sonnh.elv.dto.request.UpdateConfigReqDto;
import com.sonnh.elv.dto.response.ConfigResponseDto;
import com.sonnh.elv.service.ConfigService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/configs")
@RequiredArgsConstructor
@Tag(name = "Manage Configs")
public class ConfigController {

    private final ConfigService configService;

    @GetMapping("/{id}")
    public ResponseEntity<ConfigResponseDto> getConfigById(@PathVariable UUID id) {
        return ResponseEntity.ok(configService.getConfig(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateConfig(
            @PathVariable UUID id,
            @RequestBody UpdateConfigReqDto request
    ) {
        configService.updateConfig(id, request);
        return ResponseEntity.ok().build();
    }
}
