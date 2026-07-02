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
public class IdCardEntityMapper implements DocumentEntityMapper {

    @Override
    public DocumentType getType() {
        return DocumentType.ID_CARD;
    }

    @Override
    public void mapToEntity(Document document, Object updateDto) {
        if (updateDto == null) return;

        String documentNumber;
        LocalDate issuedDate;
        LocalDate expiredDate;
        String issuedPlace;
        Map<String, Object> metadata = new HashMap<>();

        if (updateDto instanceof DriverCreateReqDto.IdCardReqDto) {
            DriverCreateReqDto.IdCardReqDto dto = (DriverCreateReqDto.IdCardReqDto) updateDto;
            documentNumber = dto.getDocumentNumber();
            issuedDate = dto.getIssuedDate();
            expiredDate = dto.getExpiredDate();
            issuedPlace = dto.getIssuedPlace();
            
            metadata.put("dateOfBirth", dto.getDateOfBirth());
            metadata.put("sex", dto.getSex());
            metadata.put("nationality", dto.getNationality());
            metadata.put("placeOfOrigin", dto.getPlaceOfOrigin());
            metadata.put("placeOfResidence", dto.getPlaceOfResidence());
            metadata.put("personalIdentification", dto.getPersonalIdentification());
        } else if (updateDto instanceof DriverUpdateReqDto.IdCardReqDto) {
            DriverUpdateReqDto.IdCardReqDto dto = (DriverUpdateReqDto.IdCardReqDto) updateDto;
            documentNumber = dto.getDocumentNumber();
            issuedDate = dto.getIssuedDate();
            expiredDate = dto.getExpiredDate();
            issuedPlace = dto.getIssuedPlace();
            
            metadata.put("dateOfBirth", dto.getDateOfBirth());
            metadata.put("sex", dto.getSex());
            metadata.put("nationality", dto.getNationality());
            metadata.put("placeOfOrigin", dto.getPlaceOfOrigin());
            metadata.put("placeOfResidence", dto.getPlaceOfResidence());
            metadata.put("personalIdentification", dto.getPersonalIdentification());
        } else if (updateDto instanceof DriverRegisterReqDto.IdCardReqDto) {
            DriverRegisterReqDto.IdCardReqDto dto = (DriverRegisterReqDto.IdCardReqDto) updateDto;
            documentNumber = dto.getDocumentNumber();
            issuedDate = dto.getIssuedDate();
            expiredDate = dto.getExpiredDate();
            issuedPlace = dto.getIssuedPlace();
            
            metadata.put("dateOfBirth", dto.getDateOfBirth());
            metadata.put("sex", dto.getSex());
            metadata.put("nationality", dto.getNationality());
            metadata.put("placeOfOrigin", dto.getPlaceOfOrigin());
            metadata.put("placeOfResidence", dto.getPlaceOfResidence());
            metadata.put("personalIdentification", dto.getPersonalIdentification());
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
