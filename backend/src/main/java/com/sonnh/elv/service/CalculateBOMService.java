package com.sonnh.elv.service;

import com.sonnh.elv.dto.request.CalculateBOMRequestDTO;
import com.sonnh.elv.dto.response.CalculateBOMResponseDTO;

public interface CalculateBOMService {
    CalculateBOMResponseDTO calculateBOM(CalculateBOMRequestDTO dto);
}
