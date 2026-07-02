package com.sonnh.bookingcar.mapper;

import com.sonnh.bookingcar.data.domain.SpecialLocation;
import com.sonnh.bookingcar.dto.request.admin.AdminSpecialLocationReqDto;
import com.sonnh.bookingcar.dto.response.admin.AdminSpecialLocationResDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SpecialLocationMapper {

    @Mapping(target = "isActive", source = "audit.isActive")
    AdminSpecialLocationResDto toResDto(SpecialLocation specialLocation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "tourMappings", ignore = true)
    SpecialLocation toEntity(AdminSpecialLocationReqDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "tourMappings", ignore = true)
    void updateEntityFromDto(AdminSpecialLocationReqDto dto, @MappingTarget SpecialLocation entity);
}
