package com.sonnh.elv.mapper;

import com.sonnh.elv.data.domain.Tower;
import com.sonnh.elv.dto.request.CreateTowerReqDto;
import com.sonnh.elv.dto.request.UpdateTowerReqDto;
import com.sonnh.elv.dto.response.TowerResponseDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TowerMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "config.id", target = "configId")
    TowerResponseDto toTowerResponseDto(Tower tower);

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "config", ignore = true)
    Tower createDtoToTower(CreateTowerReqDto dto);

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "config", ignore = true)
    Tower updateDtoToTower(@MappingTarget Tower tower, UpdateTowerReqDto dto);
}
