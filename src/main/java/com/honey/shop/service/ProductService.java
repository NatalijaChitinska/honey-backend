package com.honey.shop.service;

import com.honey.shop.domain.Product;
import com.honey.shop.dto.response.ProductResponse;
import com.honey.shop.exception.ResourceNotFoundException;
import com.honey.shop.mapper.ProductMapper;
import com.honey.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.honey.shop.util.Constants.RESOURCE_PRODUCT;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public List<ProductResponse> findAll() {
        return productRepository.findByActiveTrue().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_PRODUCT, id));
        return productMapper.toResponse(product);
    }
}
