package com.honey.shop.mapper;

import com.honey.shop.domain.ContactMessage;
import com.honey.shop.dto.response.ContactMessageResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContactMessageMapper {
    ContactMessageResponse toResponse(ContactMessage contactMessage);
}
