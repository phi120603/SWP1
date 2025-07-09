package com.example.swp.mapper;

import com.example.swp.dto.ChatMessageResponse;
import com.example.swp.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

    @Mapper(componentModel = "spring")
    public interface ChatMapper {
        ChatMessageResponse toDto(ChatMessage entity);
    }


