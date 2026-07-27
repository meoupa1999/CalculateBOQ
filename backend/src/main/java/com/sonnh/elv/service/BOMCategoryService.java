package com.sonnh.elv.service;

import com.sonnh.elv.dto.response.CategoryResponseDTO;
import java.util.List;

public interface BOMCategoryService {
    List<CategoryResponseDTO> getCategoryTree();
}
