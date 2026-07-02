package com.sonnh.bookingcar.mapper;

import com.sonnh.bookingcar.data.domain.VehicleType;
import com.sonnh.bookingcar.dto.request.admin.VehicleTypeReqDto;
import com.sonnh.bookingcar.dto.response.admin.VehicleTypeResDto;
import com.sonnh.bookingcar.dto.response.tourist.TouristEstimateResDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VehicleTypeMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "price", ignore = true) // Set manually in service
    TouristEstimateResDto.VehiclesPricingDto toVehiclesPricingDto(VehicleType vehicleType);

    @Mapping(target = "audit", source = "audit")
    VehicleTypeResDto toResDto(VehicleType vehicleType);

    default VehicleTypeResDto.Audit mapAudit(com.sonnh.bookingcar.data.domain.embedded.Audit audit) {
        if (audit == null) return null;
        VehicleTypeResDto.Audit dto = new VehicleTypeResDto.Audit();
        dto.setCreatedAt(audit.getCreatedAt() != null ? audit.getCreatedAt().toString() : null);
        dto.setUpdatedAt(audit.getUpdatedAt() != null ? audit.getUpdatedAt().toString() : null);
        dto.setIsActive(audit.getIsActive());
        return dto;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicles", ignore = true)
    @Mapping(target = "audit", ignore = true)
    VehicleType toEntity(VehicleTypeReqDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicles", ignore = true)
    @Mapping(target = "audit", ignore = true)
    void updateEntityFromDto(VehicleTypeReqDto dto, @MappingTarget VehicleType entity);
}
