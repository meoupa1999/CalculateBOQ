package com.sonnh.bookingcar.mapper;

import com.sonnh.bookingcar.data.domain.CheckinTourHistory;
import com.sonnh.bookingcar.dto.response.admin.CheckinTourHistoryResDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CheckinTourHistoryMapper {

    @Mapping(target = "createdAt", source = "audit.createdAt")
    @Mapping(target = "driverId", source = "user.id")
    @Mapping(target = "driverName", source = "user.fullName")
    @Mapping(target = "tourId", source = "tour.id")
    @Mapping(target = "tourName", source = "tour.name")
    @Mapping(target = "specialLocationId", source = "specialLocationTour.specialLocation.id")
    @Mapping(target = "locationName", source = "specialLocationTour.specialLocation.locationName")
    @Mapping(target = "priority", source = "specialLocationTour.priority")
    CheckinTourHistoryResDto toResDto(CheckinTourHistory entity);
}
