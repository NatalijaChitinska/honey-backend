package com.honey.shop.service;

import com.honey.shop.domain.Order;
import com.honey.shop.domain.OrderItem;
import com.honey.shop.domain.Product;
import com.honey.shop.dto.request.CreateOrderRequest;
import com.honey.shop.exception.BadRequestException;
import com.honey.shop.exception.ResourceNotFoundException;
import com.honey.shop.dto.response.OrderResponse;
import com.honey.shop.mapper.OrderMapper;
import com.honey.shop.repository.OrderRepository;
import com.honey.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    private static final AtomicLong ORDER_SEQUENCE = new AtomicLong(1);

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        Instant now = Instant.now();
        String orderNumber = "ORD-" + now.getEpochSecond() + "-" + String.format("%04d", ORDER_SEQUENCE.getAndIncrement());

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .shippingAddress(request.getShippingAddress())
                .city(request.getCity())
                .customerPhone(request.getCustomerPhone())
                .status(Order.OrderStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .orderItems(new ArrayList<>())
                .build();

        for (var itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", itemRequest.getProductId()));
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName());
            }

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();
            order.getOrderItems().add(item);
        }

        order = orderRepository.save(order);
        // update ordered product total
        return orderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        return orderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponse)
                .toList();
    }
}
