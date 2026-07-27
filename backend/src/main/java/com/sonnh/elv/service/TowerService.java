package com.sonnh.elv.service;

import java.util.UUID;
import com.sonnh.elv.dto.request.CreateTowerReqDto;
import com.sonnh.elv.dto.request.UpdateTowerReqDto;
import com.sonnh.elv.dto.request.CreateTemplateReqDto;
import com.sonnh.elv.dto.response.PageImplResDto;
import com.sonnh.elv.dto.response.TowerResponseDto;
import com.sonnh.elv.dto.response.TemplateResponseDto;
import java.util.List;

public interface TowerService {
    void createTower(CreateTowerReqDto dto);

    void updateTower(UUID id, UpdateTowerReqDto dto);

    void deleteTower(UUID id);

    TowerResponseDto getTower(UUID id);

    PageImplResDto<TowerResponseDto> getAllTowers(Integer page, Integer size, String search, UUID projectId);

    void createTemplate(CreateTemplateReqDto dto);

    List<TemplateResponseDto> getAllTemplates();
}
