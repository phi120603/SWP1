package com.example.swp.service.impl;

import com.example.swp.dto.ChatMessageRequest;
import com.example.swp.dto.ChatMessageResponse;
import com.example.swp.dto.ChatThreadResponse;
import com.example.swp.entity.ChatMessage;
import com.example.swp.repository.ChatMessageRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public ChatMessageResponse save(ChatMessageRequest request) {
        String managerId = "6"; // hoặc lấy từ session nếu có phân quyền thực tế
        String roomId = generateRoomIdBetween(request.getSenderId(), managerId);
        ChatMessage message = ChatMessage.builder()
                .id(UUID.randomUUID())
                .senderId(request.getSenderId())
                .roomId(roomId)
                .content(request.getContent())
                .build();

        entityManager.persist(message); // ép luôn insert, không dùng merge

        return ChatMessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .roomId(message.getRoomId())
                .content(message.getContent())
                .mine(true)
                .createdAt(message.getCreatedAt())
                .build();
    }


    // Lấy lịch sử tin nhắn theo room
    public List<ChatMessageResponse> getMessagesByRoom(String roomId, String currentUserId) {
        List<ChatMessage> messages = chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId);

        return messages.stream().map(msg -> ChatMessageResponse.builder()
                .id(msg.getId())
                .senderId(msg.getSenderId())
                .roomId(msg.getRoomId())
                .content(msg.getContent())
                .mine(msg.getSenderId().equals(currentUserId))
                .createdAt(msg.getCreatedAt())
                .build()).collect(Collectors.toList());
    }

    public String generateRoomIdBetween(String userId1, String userId2) {
        return (userId1.compareTo(userId2) < 0)
                ? userId1 + "_" + userId2
                : userId2 + "_" + userId1;
    }

    public List<ChatThreadResponse> getAllUserThreadsWithManager(String managerId) {
        List<ChatMessage> allMessages = chatMessageRepository.findAll();

        // Chỉ lọc các room có chứa managerId
        List<ChatMessage> messages = allMessages.stream()
                .filter(m -> m.getRoomId().contains(managerId))
                .collect(Collectors.toList());

        Map<String, List<ChatMessage>> grouped = messages.stream()
                .collect(Collectors.groupingBy(ChatMessage::getRoomId));

        return grouped.entrySet().stream().map(entry -> {
            List<ChatMessage> roomMessages = entry.getValue();
            ChatMessage lastMsg = roomMessages.get(roomMessages.size() - 1);

            String peerId = lastMsg.getSenderId().equals(managerId) ? getReceiverFromRoomId(entry.getKey(), managerId) : lastMsg.getSenderId();

            return new ChatThreadResponse(
                    entry.getKey(),
                    peerId,
                    "User " + peerId,
                    lastMsg.getContent(),
                    lastMsg.getCreatedAt()
            );
        }).collect(Collectors.toList());
    }

    private String getReceiverFromRoomId(String roomId, String managerId) {
        String[] ids = roomId.split("_");
        return ids[0].equals(managerId) ? ids[1] : ids[0];
    }
}