package com.sonnh.elv.service;

import com.sonnh.elv.dto.request.CalculateBOQRequestDTO;
import com.sonnh.elv.dto.response.CalculateBOMResponseDTO;

public interface CalculateBOMService {
    CalculateBOMResponseDTO calculateBOM(CalculateBOQRequestDTO dto);
}
