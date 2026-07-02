package com.sonnh.bookingcar.mapper;

import com.sonnh.bookingcar.data.domain.Shift;
import com.sonnh.bookingcar.dto.response.admin.ShiftResDto;
import com.sonnh.bookingcar.dto.request.admin.ShiftCreateReqDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShiftMapper {

    @Mapping(target = "driverName", source = "driver.fullName")
    @Mapping(target = "driverId", source = "driver.id")
    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "vehicleInfo", expression = "java(shift.getVehicle() != null ? shift.getVehicle().getModel() + \" - \" + shift.getVehicle().getPlateNumber() : \"N/A\")")
    @Mapping(target = "started", ignore = true)
    @Mapping(target = "vehicleBusy", ignore = true)
    @Mapping(target = "conflicted", ignore = true)
    @Mapping(target = "conflictDriverName", ignore = true)
    @Mapping(target = "conflictDriverPhone", ignore = true)
    @Mapping(target = "duplicateBookingIds", ignore = true)
    ShiftResDto toShiftResDto(Shift shift);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "driver", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "airportTransferDetails", ignore = true)
    @Mapping(target = "tourBookingDetails", ignore = true)
    @Mapping(target = "shiftHistories", ignore = true)
    @Mapping(target = "bookingHistories", ignore = true)
    Shift toShift(ShiftCreateReqDto dto);
}
