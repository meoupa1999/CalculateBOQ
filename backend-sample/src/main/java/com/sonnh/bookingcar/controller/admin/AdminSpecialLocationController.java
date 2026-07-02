package com.sonnh.bookingcar.controller.admin;

import com.sonnh.bookingcar.dto.request.admin.AdminSpecialLocationReqDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.admin.AdminSpecialLocationResDto;
import com.sonnh.bookingcar.service.interfaces.AdminSpecialLocationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/special-locations")
@RequiredArgsConstructor
@Tag(name = "Manage Special Location")
public class AdminSpecialLocationController {

    private final AdminSpecialLocationService service;

    @PostMapping
    public ResponseEntity<AdminSpecialLocationResDto> create(@RequestBody AdminSpecialLocationReqDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<PageImplResDto<AdminSpecialLocationResDto>> getAll(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String search) {
        int pageIndex = (page == null || page < 1) ? 0 : page - 1;
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(pageIndex, size, Sort.by(Sort.Direction.DESC, "audit.updatedAt"));
        return ResponseEntity.ok(service.getAll(search, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminSpecialLocationResDto> getDetail(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getDetail(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminSpecialLocationResDto> update(@PathVariable UUID id, @RequestBody AdminSpecialLocationReqDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
