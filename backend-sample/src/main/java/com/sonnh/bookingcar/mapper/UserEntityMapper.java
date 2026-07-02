package com.sonnh.bookingcar.mapper;

import com.sonnh.bookingcar.data.domain.User;
import com.sonnh.bookingcar.data.domain.Document;
import com.sonnh.bookingcar.dto.request.admin.DriverCreateReqDto;
import com.sonnh.bookingcar.dto.request.admin.DriverUpdateReqDto;
import com.sonnh.bookingcar.dto.request.driver.DriverRegisterReqDto;
import com.sonnh.bookingcar.dto.response.admin.DriverResDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserEntityMapper {
    DriverResDto toDriverResDto(User user);
    DriverResDto.DocumentDto toDocumentDto(Document document);
    com.sonnh.bookingcar.dto.UserTouristDto toUserTouristDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "driverRating", ignore = true)
    @Mapping(target = "driverStatus", ignore = true)
    @Mapping(target = "shiftList", ignore = true)
    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "documents", ignore = true)
    User toUser(DriverCreateReqDto dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "driverRating", ignore = true)
    @Mapping(target = "driverStatus", ignore = true)
    @Mapping(target = "shiftList", ignore = true)
    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "documents", ignore = true)
    User toUser(DriverRegisterReqDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "driverRating", ignore = true)
    @Mapping(target = "shiftList", ignore = true)
    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "documents", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(DriverUpdateReqDto dto, @MappingTarget User user);
}
