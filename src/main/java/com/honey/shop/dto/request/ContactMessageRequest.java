package com.honey.shop.dto.request;

import com.honey.shop.domain.annotation.ValidPhone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactMessageRequest {

    @NotBlank(message = "Ова поле е задолжително")
    @Size(max = 200)
    private String fullName;

    @NotBlank(message = "Ова поле е задолжително")
    @Email(message = "Внесете валидна е-пошта!")
    @Size(max = 255)
    private String email;

    @ValidPhone
    private String phoneNumber;

    @NotBlank(message = "Ова поле е задолжително")
    @Size(max = 5000)
    private String message;

    private LocalDateTime createdAt;
}
