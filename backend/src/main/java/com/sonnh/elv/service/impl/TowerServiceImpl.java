package com.sonnh.elv.service.impl;

import com.sonnh.elv.data.domain.Config;
import com.sonnh.elv.data.domain.Project;
import com.sonnh.elv.data.domain.Tower;
import com.sonnh.elv.data.repository.ConfigRepository;
import com.sonnh.elv.data.repository.ProjectRepository;
import com.sonnh.elv.data.repository.TowerRepository;
import com.sonnh.elv.dto.request.CreateTowerReqDto;
import com.sonnh.elv.dto.request.UpdateTowerReqDto;
import com.sonnh.elv.dto.response.PageImplResDto;
import com.sonnh.elv.dto.response.TowerResponseDto;
import com.sonnh.elv.mapper.TowerMapper;
import com.sonnh.elv.service.TowerService;
import com.sonnh.elv.data.specification.TowerSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TowerServiceImpl implements TowerService {

    private final TowerRepository towerRepository;
    private final ProjectRepository projectRepository;
    private final ConfigRepository configRepository;
    private final TowerMapper towerMapper;

    @Override
    public PageImplResDto<TowerResponseDto> getAllTowers(Integer page, Integer size, String search, UUID projectId) {
        Pageable pageable = PageRequest.of(
                page != null && page > 0 ? page - 1 : 0,
                size != null && size > 0 ? size : 10,
                Sort.by(Sort.Direction.DESC, "audit.updatedAt"));

        Specification<Tower> spec = TowerSpecification.isActive();
        if (projectId != null) {
            spec = spec.and(TowerSpecification.hasProjectId(projectId));
        }
        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and(TowerSpecification.search(search));
        }

        Page<Tower> towerPage = towerRepository.findAll(spec, pageable);
        Page<TowerResponseDto> dtoPage = towerPage.map(towerMapper::toTowerResponseDto);

        return PageImplResDto.fromPage(dtoPage);
    }

    @Override
    @Transactional
    public void createTower(CreateTowerReqDto dto) {
        Project project = projectRepository.findById(dto.getProjectId()).orElseThrow();
        Config config = configRepository.findById(UUID.fromString("a2b0a797-8ff2-4a79-ac5d-78525bd25e90"))
                .orElseThrow();
        // Config config = configRepository.findById(dto.getConfigId()).orElseThrow();
        Tower tower = towerMapper.createDtoToTower(dto);
        tower.addProject(project);
        tower.addConfig(config);
        towerRepository.save(tower);
    }

    @Override
    @Transactional
    public void updateTower(UUID id, UpdateTowerReqDto dto) {
        Tower tower = towerRepository.findById(id).orElseThrow();
        towerMapper.updateDtoToTower(tower, dto);
        tower.addConfig(configRepository.findById(dto.getConfigId()).orElseThrow());
        towerRepository.save(tower);
    }

    @Override
    @Transactional
    public void deleteTower(UUID id) {
        Tower entity = towerRepository.findById(id).orElseThrow();
        entity.getAudit().setIsActive(false);
        towerRepository.save(entity);
    }

    @Override
    public TowerResponseDto getTower(UUID id) {
        return towerRepository.findById(id).map(towerMapper::toTowerResponseDto).orElseThrow();
    }
}
