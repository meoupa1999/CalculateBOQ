package com.sonnh.bookingcar.mapper;

import com.sonnh.bookingcar.data.domain.Vehicle;
import com.sonnh.bookingcar.dto.request.admin.VehicleCreateReqDto;
import com.sonnh.bookingcar.dto.request.admin.VehicleUpdateReqDto;
import com.sonnh.bookingcar.dto.response.admin.VehicleResDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VehicleMapper {
    @Mapping(target = "vehicleType", source = "vehicleType")
    VehicleResDto toVehicleResDto(Vehicle vehicle);

    VehicleResDto.VehicleTypeDto toVehicleTypeDto(com.sonnh.bookingcar.data.domain.VehicleType vehicleType);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "shiftList", ignore = true)
    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "vehicleType", ignore = true)
    Vehicle toVehicle(VehicleCreateReqDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "shiftList", ignore = true)
    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "vehicleType", ignore = true)
    void updateVehicleFromDto(VehicleUpdateReqDto dto, @MappingTarget Vehicle vehicle);
}
