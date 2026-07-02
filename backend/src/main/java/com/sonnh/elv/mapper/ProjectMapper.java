package com.sonnh.elv.mapper;

import com.sonnh.elv.data.domain.Project;
import com.sonnh.elv.dto.request.CreateProjectRequestDTO;
import com.sonnh.elv.dto.request.UpdateProjectRequestDTO;
import com.sonnh.elv.dto.response.ProjectListResponseDTO;
import com.sonnh.elv.dto.response.ProjectResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    ProjectResponseDTO toProjectResponseDTO(Project project);

    ProjectListResponseDTO toProjectListResponseDTO(Project project);

    Project createDtoToProject(CreateProjectRequestDTO dto);

    Project updateDtoToProject(@MappingTarget Project project, UpdateProjectRequestDTO dto);
}
