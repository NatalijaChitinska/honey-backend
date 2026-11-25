package com.honey.shop.service;

import com.honey.shop.domain.ContactMessage;
import com.honey.shop.dto.request.ContactMessageRequest;
import com.honey.shop.dto.response.ContactMessageResponse;
import com.honey.shop.mapper.ContactMessageMapper;
import com.honey.shop.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactMessageService {
    private final ContactMessageRepository contactMessageRepository;
    private final ContactMessageMapper contactMessageMapper;

    public ContactMessageResponse create(ContactMessageRequest request) {
        ContactMessage contactMessage = ContactMessage.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .message(request.getMessage())
                .createdAt(request.getCreatedAt())
                .build();

        ContactMessage savedContactMessage = contactMessageRepository.save(contactMessage);
        return contactMessageMapper.toResponse(savedContactMessage);
    }
}
