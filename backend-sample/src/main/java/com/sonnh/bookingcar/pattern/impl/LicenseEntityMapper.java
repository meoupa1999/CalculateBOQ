package com.sonnh.bookingcar.pattern.impl;

import com.sonnh.bookingcar.data.domain.Document;
import com.sonnh.bookingcar.data.domain.enums.DocumentType;
import com.sonnh.bookingcar.dto.request.admin.DriverCreateReqDto;
import com.sonnh.bookingcar.dto.request.admin.DriverUpdateReqDto;
import com.sonnh.bookingcar.dto.request.driver.DriverRegisterReqDto;
import com.sonnh.bookingcar.pattern.interfaces.DocumentEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LicenseEntityMapper implements DocumentEntityMapper {

    @Override
    public DocumentType getType() {
        return DocumentType.LICENSE;
    }

    @Override
    public void mapToEntity(Document document, Object updateDto) {
        if (updateDto == null) return;

        String documentNumber;
        LocalDate issuedDate;
        LocalDate expiredDate;
        String issuedPlace;
        Map<String, Object> metadata = new HashMap<>();

        if (updateDto instanceof DriverCreateReqDto.LicenseReqDto) {
            DriverCreateReqDto.LicenseReqDto dto = (DriverCreateReqDto.LicenseReqDto) updateDto;
            documentNumber = dto.getDocumentNumber();
            issuedDate = dto.getIssuedDate();
            expiredDate = dto.getExpiredDate();
            issuedPlace = dto.getIssuedPlace();
            
            metadata.put("fullName", dto.getFullName());
            metadata.put("dateOfBirth", dto.getDateOfBirth());
            metadata.put("nationality", dto.getNationality());
            metadata.put("address", dto.getAddress());
        } else if (updateDto instanceof DriverUpdateReqDto.LicenseReqDto) {
            DriverUpdateReqDto.LicenseReqDto dto = (DriverUpdateReqDto.LicenseReqDto) updateDto;
            documentNumber = dto.getDocumentNumber();
            issuedDate = dto.getIssuedDate();
            expiredDate = dto.getExpiredDate();
            issuedPlace = dto.getIssuedPlace();
            
            metadata.put("fullName", dto.getFullName());
            metadata.put("dateOfBirth", dto.getDateOfBirth());
            metadata.put("nationality", dto.getNationality());
            metadata.put("address", dto.getAddress());
        } else if (updateDto instanceof DriverRegisterReqDto.LicenseReqDto) {
            DriverRegisterReqDto.LicenseReqDto dto = (DriverRegisterReqDto.LicenseReqDto) updateDto;
            documentNumber = dto.getDocumentNumber();
            issuedDate = dto.getIssuedDate();
            expiredDate = dto.getExpiredDate();
            issuedPlace = dto.getIssuedPlace();
            
            metadata.put("fullName", dto.getFullName());
            metadata.put("dateOfBirth", dto.getDateOfBirth());
            metadata.put("nationality", dto.getNationality());
            metadata.put("address", dto.getAddress());
        } else {
            throw new IllegalArgumentException("Unsupported DTO type: " + updateDto.getClass().getName());
        }

        document.setDocumentNumber(documentNumber);
        document.setIssuedDate(issuedDate);
        document.setExpiredDate(expiredDate);
        document.setIssuedPlace(issuedPlace);
        document.setMetadata(metadata);
    }
}
