package com.sonnh.elv.controller;

import com.sonnh.elv.dto.request.CreateTowerReqDto;
import com.sonnh.elv.dto.request.UpdateTowerReqDto;
import com.sonnh.elv.dto.response.PageImplResDto;
import com.sonnh.elv.dto.response.TowerResponseDto;
import com.sonnh.elv.service.TowerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/towers")
@RequiredArgsConstructor
@Tag(name = "Manage Towers")
public class TowerController {

    private final TowerService towerService;

    @PostMapping
    public ResponseEntity<Void> createTower(@RequestBody CreateTowerReqDto request) {
        towerService.createTower(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<PageImplResDto<TowerResponseDto>> getAllTowers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID projectId
    ) {
        return ResponseEntity.ok(towerService.getAllTowers(page, size, search, projectId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TowerResponseDto> getTowerById(@PathVariable UUID id) {
        return ResponseEntity.ok(towerService.getTower(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTower(
            @PathVariable UUID id,
            @RequestBody UpdateTowerReqDto request
    ) {
        towerService.updateTower(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTower(@PathVariable UUID id) {
        towerService.deleteTower(id);
        return ResponseEntity.noContent().build();
    }
}
