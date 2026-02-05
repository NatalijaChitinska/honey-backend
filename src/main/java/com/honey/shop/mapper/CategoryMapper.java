package com.honey.shop.mapper;

import com.honey.shop.domain.Category;
import com.honey.shop.dto.response.CategoryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);
}
