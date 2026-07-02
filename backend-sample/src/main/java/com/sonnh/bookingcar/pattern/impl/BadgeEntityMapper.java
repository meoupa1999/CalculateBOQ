package com.sonnh.bookingcar.pattern.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonnh.bookingcar.data.domain.Document;
import com.sonnh.bookingcar.data.domain.enums.DocumentType;
import com.sonnh.bookingcar.dto.request.admin.VehicleCreateReqDto;
import com.sonnh.bookingcar.dto.request.admin.VehicleUpdateReqDto;
import com.sonnh.bookingcar.pattern.interfaces.DocumentEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class BadgeEntityMapper implements DocumentEntityMapper {
    private final ObjectMapper objectMapper;

    @Override
    public DocumentType getType() {
        return DocumentType.BADGE;
    }

    @Override
    public void mapToEntity(Document document, Object updateDto) {
        if (updateDto == null) return;
        
        String agency;
        String documentNumber;
        java.time.LocalDate issuedDate;
        java.time.LocalDate expiredDate;

        if (updateDto instanceof VehicleCreateReqDto.BadgeReqDto) {
            VehicleCreateReqDto.BadgeReqDto dto = (VehicleCreateReqDto.BadgeReqDto) updateDto;
            documentNumber = dto.getDocumentNumber();
            issuedDate = dto.getIssuedDate();
            expiredDate = dto.getExpiredDate();
            agency = dto.getAgency();
        } else if (updateDto instanceof VehicleUpdateReqDto.BadgeReqDto) {
            VehicleUpdateReqDto.BadgeReqDto dto = (VehicleUpdateReqDto.BadgeReqDto) updateDto;
            documentNumber = dto.getDocumentNumber();
            issuedDate = dto.getIssuedDate();
            expiredDate = dto.getExpiredDate();
            agency = dto.getAgency();
        } else {
            throw new IllegalArgumentException("Unsupported DTO type: " + updateDto.getClass().getName());
        }

        document.setDocumentNumber(documentNumber);
        document.setIssuedDate(issuedDate);
        document.setExpiredDate(expiredDate);
        document.setMetadata(Map.of("agency", agency != null ? agency : ""));
    }
}
