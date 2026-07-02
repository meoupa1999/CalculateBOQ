package com.sonnh.bookingcar.controller.admin;

import com.sonnh.bookingcar.dto.response.DocumentDashboardResponse;
import com.sonnh.bookingcar.dto.response.DocumentDetailResponseDto;
import com.sonnh.bookingcar.dto.response.DocumentListResponseDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.service.interfaces.DocumentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import com.sonnh.bookingcar.dto.response.DriverDropdownResDto;
import com.sonnh.bookingcar.dto.response.VehicleDropdownResDto;
import com.sonnh.bookingcar.data.domain.enums.DocumentOwnerType;
import com.sonnh.bookingcar.data.domain.enums.DocumentType;
import com.sonnh.bookingcar.data.domain.enums.DocumentStatus;
import com.sonnh.bookingcar.dto.request.admin.DocumentCreateReqDto;
import com.sonnh.bookingcar.dto.request.admin.DocumentUpdateReqDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/documents")
@RequiredArgsConstructor
@Tag(name = "manage document")
public class AdminDocumentController {

    private final DocumentService documentService;

    @GetMapping("/dropdown/drivers")
    public ResponseEntity<List<DriverDropdownResDto>> getDriverDropdown() {
        return ResponseEntity.ok(documentService.getDriverDropdown());
    }

    @GetMapping("/dropdown/vehicles")
    public ResponseEntity<List<VehicleDropdownResDto>> getVehicleDropdown() {
        return ResponseEntity.ok(documentService.getVehicleDropdown());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDetailResponseDto> getDocumentById(@PathVariable UUID id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DocumentDashboardResponse> getDashboard(
            @RequestParam(required = false) DocumentOwnerType ownerType) {
        return ResponseEntity.ok(documentService.getDashboard(ownerType));
    }

    @GetMapping
    public ResponseEntity<PageImplResDto<DocumentListResponseDto>> getDocuments(
            @RequestParam(required = false) DocumentOwnerType ownerType,
            @RequestParam(required = false) DocumentType documentType,
            @RequestParam(required = false) DocumentStatus status,
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(documentService.getDocuments(ownerType, documentType, status, searchKeyword, page, size));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentDetailResponseDto> createDocument(
            @RequestPart("document") DocumentCreateReqDto dto,
            @RequestPart(value = "frontImage", required = false) MultipartFile frontImage,
            @RequestPart(value = "backImage", required = false) MultipartFile backImage) {
        return ResponseEntity.ok(documentService.createDocument(dto, frontImage, backImage));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentDetailResponseDto> updateDocument(
            @PathVariable UUID id,
            @RequestPart("document") DocumentUpdateReqDto dto,
            @RequestPart(value = "frontImage", required = false) MultipartFile frontImage,
            @RequestPart(value = "backImage", required = false) MultipartFile backImage) {
        return ResponseEntity.ok(documentService.updateDocument(id, dto, frontImage, backImage));
    }
}
