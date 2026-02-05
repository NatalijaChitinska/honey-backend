package com.honey.shop.mapper;

import com.honey.shop.domain.Product;
import com.honey.shop.dto.response.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface ProductMapper {

    @Mapping(target = "category", source = "category")
    ProductResponse toResponse(Product product);
}
