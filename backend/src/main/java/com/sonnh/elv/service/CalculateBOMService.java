package com.sonnh.elv.service;

import com.sonnh.elv.dto.request.CalculateBOMRequestDTO;
import com.sonnh.elv.dto.response.CalculateBOMResponseDTO;

import java.util.List;

public interface CalculateBOMService {
    CalculateBOMResponseDTO calculateBOM(List<CalculateBOMRequestDTO> dtos);
}
