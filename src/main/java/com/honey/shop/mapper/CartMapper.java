package com.honey.shop.mapper;

import com.honey.shop.domain.Cart;
import com.honey.shop.domain.CartItem;
import com.honey.shop.dto.response.CartItemResponse;
import com.honey.shop.dto.response.CartResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "totalPrice", expression = "java(computeTotal(cart))")
    CartResponse toResponse(Cart cart);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "unitPrice", source = "product.price")
    @Mapping(target = "subtotal", expression = "java(subtotal(item))")
    CartItemResponse toItemResponse(CartItem item);

    default BigDecimal subtotal(CartItem item) {
        return item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    }

    default BigDecimal computeTotal(Cart cart) {
        return cart.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
