package com.honey.shop.dto.request;

import com.honey.shop.domain.annotation.ValidPhone;
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

    @NotBlank(message = "Ова поле е задолжително")
    @Size(max = 200)
    private String customerName;

    @NotBlank(message = "Ова поле е задолжително")
    @Email(message = "Внесете валидна е-пошта!")
    @Size(max = 255)
    private String customerEmail;

    @NotBlank(message = "Ова поле е задолжително")
    @Size(max = 500)
    private String shippingAddress;

    @NotBlank(message = "Ова поле е задолжително")
    @Size(max = 500)
    private String city;

    @NotBlank(message = "Ова поле е задолжително")
    @ValidPhone
    private String customerPhone;

    @NotEmpty(message = "At least one order item is required")
    @Size(max = 50, message = "Order cannot contain more than 50 items")
    @Valid
    private List<OrderItemRequest> items;
}
