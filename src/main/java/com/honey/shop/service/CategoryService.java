package com.honey.shop.service;

import com.honey.shop.domain.Category;
import com.honey.shop.dto.response.CategoryResponse;
import com.honey.shop.exception.ResourceNotFoundException;
import com.honey.shop.mapper.CategoryMapper;
import com.honey.shop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.honey.shop.util.Constants.RESOURCE_CATEGORY;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_CATEGORY, id));
        return categoryMapper.toResponse(category);
    }
}
