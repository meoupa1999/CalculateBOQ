package com.sonnh.elv.service;

import java.util.List;

import com.sonnh.elv.dto.request.CalculateBOQRequestDTO;
import com.sonnh.elv.dto.response.CalculateBOQResponseDTO;

public interface CalculateService {

    List<CalculateBOQResponseDTO> calculateBOQ(CalculateBOQRequestDTO dto);

}
