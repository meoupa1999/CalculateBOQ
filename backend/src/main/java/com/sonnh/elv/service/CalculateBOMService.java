package com.sonnh.elv.service;

import com.sonnh.elv.dto.request.CalculateBOMRequestDTO;
import java.util.List;
import java.util.Map;

public interface CalculateBOMService {
    Map<String, Integer> calculateBOM(List<CalculateBOMRequestDTO> dtos);
}
