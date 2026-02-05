package com.honey.shop.dto.response;

import com.honey.shop.domain.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private String orderNumber;
    private String customerName;
    private String customerEmail;
    private String shippingAddress;
    private Order.OrderStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private List<OrderItemResponse> orderItems;
}
