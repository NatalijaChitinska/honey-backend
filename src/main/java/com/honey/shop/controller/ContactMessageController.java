package com.honey.shop.controller;

import com.honey.shop.dto.request.ContactMessageRequest;
import com.honey.shop.dto.response.ApiResponse;
import com.honey.shop.dto.response.ContactMessageResponse;
import com.honey.shop.service.ContactMessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact-messages")
public class ContactMessageController {
    private final ContactMessageService contactMessageService;

    public ContactMessageController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ContactMessageResponse>> getContactMessageById(@Valid @RequestBody ContactMessageRequest request) {
        ContactMessageResponse created = contactMessageService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Contact message created"));
    }
}
