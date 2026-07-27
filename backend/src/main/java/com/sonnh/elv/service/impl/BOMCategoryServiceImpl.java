package com.sonnh.elv.service.impl;

import com.sonnh.elv.data.domain.Category;
import com.sonnh.elv.data.repository.CategoryRepository;
import com.sonnh.elv.dto.response.CategoryResponseDTO;
import com.sonnh.elv.mapper.BOMMapper;
import com.sonnh.elv.service.BOMCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BOMCategoryServiceImpl implements BOMCategoryService {

    private final CategoryRepository categoryRepository;
    private final BOMMapper bomMapper;

    @Override
    public List<CategoryResponseDTO> getCategoryTree() {
        List<Category> rootCategories = categoryRepository.findByParentIsNullOrderByOrderIndexAsc();
        return bomMapper.toCategoryResponseDTOList(rootCategories);
    }
}
