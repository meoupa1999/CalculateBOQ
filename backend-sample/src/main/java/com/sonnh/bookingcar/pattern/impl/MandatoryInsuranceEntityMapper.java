package com.sonnh.bookingcar.pattern.impl;

import com.sonnh.bookingcar.data.domain.Document;
import com.sonnh.bookingcar.data.domain.enums.DocumentType;
import com.sonnh.bookingcar.dto.request.admin.VehicleCreateReqDto;
import com.sonnh.bookingcar.dto.request.admin.VehicleUpdateReqDto;
import com.sonnh.bookingcar.pattern.interfaces.DocumentEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MandatoryInsuranceEntityMapper implements DocumentEntityMapper {

    @Override
    public DocumentType getType() {
        return DocumentType.MANDATORY_INSURANCE;
    }

    @Override
    public void mapToEntity(Document document, Object updateDto) {
        if (updateDto == null) return;

        String documentNumber;
        java.time.LocalDate issuedDate;
        java.time.LocalDate expiredDate;
        Map<String, Object> metadata = new HashMap<>();

        if (updateDto instanceof VehicleCreateReqDto.MandatoryInsuranceReqDto) {
            VehicleCreateReqDto.MandatoryInsuranceReqDto dto = (VehicleCreateReqDto.MandatoryInsuranceReqDto) updateDto;
            documentNumber = dto.getDocumentNumber();
            issuedDate = dto.getIssuedDate();
            expiredDate = dto.getExpiredDate();
            
            metadata.put("ownerName", dto.getOwnerName());
            metadata.put("address", dto.getAddress());
            metadata.put("phoneNumber", dto.getPhoneNumber());
            metadata.put("plateNumber", dto.getPlateNumber());
            metadata.put("chassisNumber", dto.getChassisNumber());
            metadata.put("engineNumber", dto.getEngineNumber());
            metadata.put("vehicleType", dto.getVehicleType());
            metadata.put("payload", dto.getPayload());
            metadata.put("seats", dto.getSeats());
            metadata.put("usagePurpose", dto.getUsagePurpose());
            metadata.put("insuranceFee", dto.getInsuranceFee());
            metadata.put("insuranceFeeVAT", dto.getInsuranceFeeVAT());
            metadata.put("issuer", dto.getIssuer());
        } else if (updateDto instanceof VehicleUpdateReqDto.MandatoryInsuranceReqDto) {
            VehicleUpdateReqDto.MandatoryInsuranceReqDto dto = (VehicleUpdateReqDto.MandatoryInsuranceReqDto) updateDto;
            documentNumber = dto.getDocumentNumber();
            issuedDate = dto.getIssuedDate();
            expiredDate = dto.getExpiredDate();
            
            metadata.put("ownerName", dto.getOwnerName());
            metadata.put("address", dto.getAddress());
            metadata.put("phoneNumber", dto.getPhoneNumber());
            metadata.put("plateNumber", dto.getPlateNumber());
            metadata.put("chassisNumber", dto.getChassisNumber());
            metadata.put("engineNumber", dto.getEngineNumber());
            metadata.put("vehicleType", dto.getVehicleType());
            metadata.put("payload", dto.getPayload());
            metadata.put("seats", dto.getSeats());
            metadata.put("usagePurpose", dto.getUsagePurpose());
            metadata.put("insuranceFee", dto.getInsuranceFee());
            metadata.put("insuranceFeeVAT", dto.getInsuranceFeeVAT());
            metadata.put("issuer", dto.getIssuer());
        } else {
            throw new IllegalArgumentException("Unsupported DTO type: " + updateDto.getClass().getName());
        }

        document.setDocumentNumber(documentNumber);
        document.setIssuedDate(issuedDate);
        document.setExpiredDate(expiredDate);
        document.setMetadata(metadata);
    }
}
