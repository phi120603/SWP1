//package com.example.swp.dto;
//
//import com.example.swp.entity.ChatMessage;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//
//@Mapper(componentModel = "spring")
//public interface ChatMapper {
//    @Mapping(target = "messageId", source = "id")
//    @Mapping(target = "sessionId", source = "session.id")
//    @Mapping(target = "sender", source = "sender")
//    @Mapping(target = "timestamp", expression = "java(m.getCreatedAt().toEpochMilli())")
//    ChatMessageResponse toDto(ChatMessage m);
//}
