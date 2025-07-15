package com.example.swp.service;

import com.example.swp.dto.MessageDto;
import com.example.swp.dto.SendMessageRequest;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Manager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MessageService {

    // Send message from customer
    MessageDto sendMessageFromCustomer(Customer customer, SendMessageRequest request);

    // Send message from manager
    MessageDto sendMessageFromManager(Manager manager, SendMessageRequest request);

    // Get messages in conversation
    List<MessageDto> getConversationMessages(UUID conversationId);
    Page<MessageDto> getConversationMessages(UUID conversationId, Pageable pageable);

    // Get messages after specific time (for real-time updates)
    List<MessageDto> getMessagesAfter(UUID conversationId, LocalDateTime after);

    // Mark message as read
    void markMessageAsRead(UUID messageId, Integer userId, String userType);

    // Search messages in conversation
    List<MessageDto> searchMessages(UUID conversationId, String searchTerm);

    // Get message by ID
    MessageDto getMessageById(UUID messageId);

    // Get messages with attachments
    List<MessageDto> getMessagesWithAttachments(UUID conversationId);

    // Get unread message count
    long getUnreadMessageCount(UUID conversationId);
}
