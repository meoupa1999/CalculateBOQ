package com.sonnh.bookingcar.service.interfaces;

import com.sonnh.bookingcar.data.domain.enums.DocumentOwnerType;
import com.sonnh.bookingcar.data.domain.enums.DocumentStatus;
import com.sonnh.bookingcar.data.domain.enums.DocumentType;
import com.sonnh.bookingcar.dto.response.DocumentDashboardResponse;
import com.sonnh.bookingcar.dto.response.DocumentDetailResponseDto;
import com.sonnh.bookingcar.dto.response.DocumentListResponseDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;

import com.sonnh.bookingcar.dto.request.admin.DocumentCreateReqDto;
import com.sonnh.bookingcar.dto.request.admin.DocumentUpdateReqDto;
import com.sonnh.bookingcar.dto.response.DriverDropdownResDto;
import com.sonnh.bookingcar.dto.response.VehicleDropdownResDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
    PageImplResDto<DocumentListResponseDto> getDocuments(
            DocumentOwnerType ownerType,
            DocumentType documentType,
            DocumentStatus status,
            String searchKeyword,
            Integer page,
            Integer size);

    DocumentDashboardResponse getDashboard(DocumentOwnerType ownerType);

    DocumentDetailResponseDto getDocumentById(java.util.UUID id);

    DocumentDetailResponseDto createDocument(DocumentCreateReqDto dto, MultipartFile frontImage, MultipartFile backImage);

    DocumentDetailResponseDto updateDocument(java.util.UUID id, DocumentUpdateReqDto dto, MultipartFile frontImage, MultipartFile backImage);

    List<DriverDropdownResDto> getDriverDropdown();

    List<VehicleDropdownResDto> getVehicleDropdown();
}
