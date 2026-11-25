package com.honey.shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactMessageResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String message;
    private LocalDateTime createdAt;
}
