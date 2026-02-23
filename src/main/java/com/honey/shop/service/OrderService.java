package com.honey.shop.service;

import com.honey.shop.domain.Order;
import com.honey.shop.domain.OrderItem;
import com.honey.shop.domain.Product;
import com.honey.shop.domain.enumerations.EmailTemplate;
import com.honey.shop.dto.request.CreateOrderRequest;
import com.honey.shop.exception.BadRequestException;
import com.honey.shop.exception.ResourceNotFoundException;
import com.honey.shop.dto.response.OrderResponse;
import com.honey.shop.mapper.OrderMapper;
import com.honey.shop.repository.OrderRepository;
import com.honey.shop.repository.ProductRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.honey.shop.util.Constants.*;
import static org.apache.commons.lang3.StringUtils.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final EmailService emailService;

    private static final AtomicLong ORDER_SEQUENCE = new AtomicLong(1);
    public static final int DELIVERY = 150;

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
                .notes(request.getNotes())
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
        var response = orderMapper.toResponse(order);

        sendReceiptToUser(order);

        return response;
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_ORDER, id));
        return orderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    private void sendReceiptToUser(Order order) {

        String itemsText = order.getOrderItems().stream()
                .map(item -> String.format(
                        "%s x %d (%s ден.)",
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice().toString()
                ))
                .collect(Collectors.joining("<br>"));

        BigDecimal total = order.getOrderItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalQuantity = order.getOrderItems().stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        HashMap<String, String> content = new HashMap<>();
        String deliveryHtml = EMPTY;
        StringBuilder notes = new StringBuilder();
        String deliveryNotes = EMPTY;

        if (totalQuantity <=4 ) {
            if (!order.getCity().contains(CITY_BEROVO)) {
                total =  total.add(BigDecimal.valueOf(DELIVERY));
                deliveryHtml = formatDeliveryHtml();
            } else{
                deliveryNotes = DELIVERY_NOTES_STRING;
            }
        }
        if (order.getNotes() != null && StringUtils.isNotBlank(order.getNotes())){
            notes.append(formatNotesHtml(order.getNotes(), deliveryNotes));
        }

        content.put("name", order.getCustomerName());
        content.put("order", itemsText);
        content.put("total", total.toString());
        content.put("orderNumber", order.getOrderNumber());
        content.put("address", order.getShippingAddress() + " - " + order.getCity());
        content.put("phone", order.getCustomerPhone());
        content.put("deliveryHtml", deliveryHtml);
        content.put("notes", notes.toString().isEmpty() ? deliveryNotes: notes.toString());

        emailService.sendHtmlEmail(content, EmailTemplate.USER_ORDER_RECEIPT, order.getCustomerEmail());
    }

    private String formatDeliveryHtml() {
        return String.format(
                DELIVERY_HTML,
                BigDecimal.valueOf(DELIVERY));
    }

    private String formatNotesHtml(String notes, String deliveryNotes) {
        return String.format(
                NOTES_HTML,
                notes, deliveryNotes);
    }
}
