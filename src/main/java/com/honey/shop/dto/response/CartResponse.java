package com.honey.shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {

    private Long id;
    private String token;
    private Instant createdAt;
    private Instant updatedAt;
    private List<CartItemResponse> items;
    private BigDecimal totalPrice;
}
