package com.sonnh.elv.service;

import java.util.List;

import com.sonnh.elv.dto.request.CalculateBOQRequestDTO;
import com.sonnh.elv.dto.response.CalculateBOQResponseDTO;
import com.sonnh.elv.dto.response.CalculateBOMResponseDTO;

public interface CalculateService {

    List<CalculateBOQResponseDTO> calculateBOQ(CalculateBOQRequestDTO dto);

    CalculateBOMResponseDTO calculateBOM(CalculateBOQRequestDTO dto);

}
