package com.sonnh.bookingcar.pattern.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonnh.bookingcar.data.domain.Document;
import com.sonnh.bookingcar.data.domain.enums.DocumentType;
import com.sonnh.bookingcar.data.domain.enums.ImageType;
import com.sonnh.bookingcar.dto.response.admin.DriverResDto;
import com.sonnh.bookingcar.pattern.interfaces.DocumentDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LicenseDtoMapper implements DocumentDtoMapper {
    private final ObjectMapper objectMapper;

    @Override
    public DocumentType getType() {
        return DocumentType.LICENSE;
    }

    @Override
    public void map(Object resDto, Document doc) {
        DriverResDto driverResDto = (DriverResDto) resDto;
        DriverResDto.LicenseDto metadataDto = objectMapper.convertValue(doc.getMetadata(), DriverResDto.LicenseDto.class);
        final DriverResDto.LicenseDto licenseDto = (metadataDto != null) ? metadataDto : new DriverResDto.LicenseDto();

        DriverResDto.DocumentDto documentDto = driverResDto.getDocumentDto();
        if (documentDto == null) {
            documentDto = new DriverResDto.DocumentDto();
            driverResDto.setDocumentDto(documentDto);
        }
        
        licenseDto.setDocumentNumber(doc.getDocumentNumber());
        licenseDto.setIssuedDate(doc.getIssuedDate());
        licenseDto.setExpiredDate(doc.getExpiredDate());
        licenseDto.setIssuedPlace(doc.getIssuedPlace());
        licenseDto.setOwnerType(doc.getOwnerType());
        licenseDto.setDocumentType(doc.getDocumentType());
        licenseDto.setId(doc.getId());

        // Map Document Images
        doc.getImages().stream()
                .filter(img -> img.getImageType() == ImageType.LICENSE_FRONT)
                .findFirst()
                .ifPresent(img -> licenseDto.setFrontImagePath(img.getFilePath()));

        documentDto.setLicenseDto(licenseDto);
    }
}
