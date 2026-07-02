package com.sonnh.elv.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.sonnh.elv.data.domain.Project;
import com.sonnh.elv.data.repository.ProjectRepository;
import com.sonnh.elv.data.specification.ProjectSpecification;
import com.sonnh.elv.dto.request.CreateProjectRequestDTO;
import com.sonnh.elv.dto.request.UpdateProjectRequestDTO;
import com.sonnh.elv.dto.response.PageImplResDto;
import com.sonnh.elv.dto.response.ProjectListResponseDTO;
import com.sonnh.elv.dto.response.ProjectResponseDTO;
import com.sonnh.elv.mapper.ProjectMapper;
import com.sonnh.elv.service.ProjectService;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public void create(CreateProjectRequestDTO dto) {
        projectRepository.save(projectMapper.createDtoToProject(dto));
    }

    @Override
    public PageImplResDto<ProjectListResponseDTO> findAll(Integer page, Integer size, String search) {
        Pageable pageable = PageRequest.of(
            page != null && page > 0 ? page - 1 : 0,
            size != null && size > 0 ? size : 10,
            Sort.by(Sort.Direction.DESC, "audit.updatedAt")
        );

        Specification<Project> spec = ProjectSpecification.isActive();
        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and(ProjectSpecification.search(search));
        }

        Page<Project> projectPage = projectRepository.findAll(spec, pageable);
        Page<ProjectListResponseDTO> dtoPage = projectPage.map(projectMapper::toProjectListResponseDTO);

        return PageImplResDto.fromPage(dtoPage);
    }

    @Override
    public ProjectResponseDTO findById(UUID id) {
        return projectRepository.findById(id).map(projectMapper::toProjectResponseDTO)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
    }

    @Override
    public void update(UUID id, UpdateProjectRequestDTO dto) {
        Project entity = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        entity = projectMapper.updateDtoToProject(entity, dto);
        projectRepository.save(entity);
    }

    @Override
    public void delete(UUID id) {
        Project entity = projectRepository.findById(id).get();
        entity.getAudit().setIsActive(false);
        projectRepository.save(entity);
    }

}
