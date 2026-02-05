package com.honey.shop.controller;

import com.honey.shop.dto.response.ApiResponse;
import com.honey.shop.dto.response.ProductResponse;
import com.honey.shop.service.ProductService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAll(
            @RequestParam(required = false) @Positive(message = "Category ID must be positive") Long categoryId) {
        if (categoryId != null) {
            return ResponseEntity.ok(ApiResponse.success(productService.findByCategoryId(categoryId)));
        }
        return ResponseEntity.ok(ApiResponse.success(productService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(
            @PathVariable @Positive(message = "Product ID must be positive") Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.findById(id)));
    }
}
