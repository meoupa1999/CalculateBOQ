package com.sonnh.elv.service;

import java.util.List;
import java.util.UUID;

import com.sonnh.elv.dto.request.CalculateBOQRequestDTO;
import com.sonnh.elv.dto.request.CalculateBOQManualRequestDTO;
import com.sonnh.elv.dto.response.CalculateBOQResponseDTO;

public interface CalculateService {

    List<CalculateBOQResponseDTO> calculateBOQ(UUID towerId, CalculateBOQRequestDTO dto);

    List<CalculateBOQResponseDTO> calculateBOQManual(UUID towerId, CalculateBOQManualRequestDTO dto);

    List<CalculateBOQResponseDTO> getCalculateBOQ(UUID towerId);

}
