package com.sonnh.elv.service;

import java.util.UUID;
import com.sonnh.elv.dto.request.CreateProjectRequestDTO;
import com.sonnh.elv.dto.request.UpdateProjectRequestDTO;
import com.sonnh.elv.dto.response.PageImplResDto;
import com.sonnh.elv.dto.response.ProjectListResponseDTO;
import com.sonnh.elv.dto.response.ProjectResponseDTO;

public interface ProjectService {

    void create(CreateProjectRequestDTO request);

    PageImplResDto<ProjectListResponseDTO> findAll(Integer page, Integer size, String search);

    ProjectResponseDTO findById(UUID id);

    void update(UUID id, UpdateProjectRequestDTO request);

    void delete(UUID id);
}
