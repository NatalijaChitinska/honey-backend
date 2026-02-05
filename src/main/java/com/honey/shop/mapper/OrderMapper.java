package com.honey.shop.mapper;

import com.honey.shop.domain.Order;
import com.honey.shop.domain.OrderItem;
import com.honey.shop.dto.response.OrderItemResponse;
import com.honey.shop.dto.response.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse toResponse(Order order);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "subtotal", expression = "java(subtotal(orderItem))")
    OrderItemResponse toItemResponse(OrderItem orderItem);

    default BigDecimal subtotal(OrderItem item) {
        return item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    }
}
