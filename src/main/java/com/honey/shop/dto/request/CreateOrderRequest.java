package com.honey.shop.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    @NotBlank(message = "Customer name is required")
    @Size(max = 200)
    private String customerName;

    @NotBlank(message = "Customer email is required")
    @Email
    @Size(max = 255)
    private String customerEmail;

    @NotBlank(message = "Shipping address is required")
    @Size(max = 500)
    private String shippingAddress;

    @NotEmpty(message = "At least one order item is required")
    @Size(max = 50, message = "Order cannot contain more than 50 items")
    @Valid
    private List<OrderItemRequest> items;
}
