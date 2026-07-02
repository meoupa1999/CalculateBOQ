package com.sonnh.bookingcar.mapper;

import com.sonnh.bookingcar.data.domain.Document;
import com.sonnh.bookingcar.dto.response.DocumentListResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentMapper {

    @Mapping(target = "expiryDate", source = "expiredDate")
    @Mapping(target = "updatedAt", source = "audit.updatedAt")
    @Mapping(target = "driver", expression = "java(mapDriver(document))")
    @Mapping(target = "vehicle", expression = "java(mapVehicle(document))")
    @Mapping(target = "status", ignore = true)
    DocumentListResponseDto toListResponse(Document document);

    @Mapping(target = "expiredDate", source = "expiredDate")
    @Mapping(target = "createdAt", source = "audit.createdAt")
    @Mapping(target = "updatedAt", source = "audit.updatedAt")
    @Mapping(target = "driver", expression = "java(mapDetailDriver(document))")
    @Mapping(target = "vehicle", expression = "java(mapDetailVehicle(document))")
    @Mapping(target = "status", ignore = true)
    com.sonnh.bookingcar.dto.response.DocumentDetailResponseDto toDetailResponse(Document document);

    default DocumentListResponseDto.DriverInfo mapDriver(Document document) {
        if (document.getUser() == null) return null;
        return DocumentListResponseDto.DriverInfo.builder()
                .id(document.getUser().getId())
                .fullName(document.getUser().getFullName())
                .build();
    }

    default DocumentListResponseDto.VehicleInfo mapVehicle(Document document) {
        if (document.getVehicle() == null) return null;
        return DocumentListResponseDto.VehicleInfo.builder()
                .id(document.getVehicle().getId())
                .plateNumber(document.getVehicle().getPlateNumber())
                .build();
    }

    default com.sonnh.bookingcar.dto.response.DocumentDetailResponseDto.DriverInfo mapDetailDriver(Document document) {
        if (document.getUser() == null) return null;
        return com.sonnh.bookingcar.dto.response.DocumentDetailResponseDto.DriverInfo.builder()
                .id(document.getUser().getId())
                .fullName(document.getUser().getFullName())
                .build();
    }

    default com.sonnh.bookingcar.dto.response.DocumentDetailResponseDto.VehicleInfo mapDetailVehicle(Document document) {
        if (document.getVehicle() == null) return null;
        return com.sonnh.bookingcar.dto.response.DocumentDetailResponseDto.VehicleInfo.builder()
                .id(document.getVehicle().getId())
                .plateNumber(document.getVehicle().getPlateNumber())
                .build();
    }
}
