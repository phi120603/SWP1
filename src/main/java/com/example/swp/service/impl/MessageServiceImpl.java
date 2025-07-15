package com.example.swp.service.impl;

import com.example.swp.dto.MessageDto;
import com.example.swp.dto.SendMessageRequest;
import com.example.swp.entity.Conversation;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Manager;
import com.example.swp.entity.Message;
import com.example.swp.enums.SenderType;
import com.example.swp.repository.ConversationRepository;
import com.example.swp.repository.MessageRepository;
import com.example.swp.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;

    @Override
    public MessageDto sendMessageFromCustomer(Customer customer, SendMessageRequest request) {
        Conversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        // Verify customer has access to this conversation
        if (!(conversation.getCustomer().getId() == customer.getId())) {
            throw new RuntimeException("Access denied");
        }

        Message message = Message.builder()
                .conversation(conversation)
                .senderId(customer.getId())
                .senderType(SenderType.CUSTOMER)
                .content(request.getContent())
                .messageType(request.getMessageType())
                .attachmentUrl(request.getAttachmentUrl())
                .attachmentName(request.getAttachmentName())
                .attachmentSize(request.getAttachmentSize())
                .isRead(false)
                .build();

        message = messageRepository.save(message);

        // Update conversation
        conversation.updateLastMessageTime();
        conversation.markAsUnreadForManager();
        conversationRepository.save(conversation);

        return convertToDto(message, customer.getId(), SenderType.CUSTOMER);
    }

    @Override
    public MessageDto sendMessageFromManager(Manager manager, SendMessageRequest request) {
        Conversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        // Verify manager has access to this conversation
        if (conversation.getManager() == null || !(conversation.getManager().getId() == manager.getId())) {
            throw new RuntimeException("Access denied");
        }

        Message message = Message.builder()
                .conversation(conversation)
                .senderId(manager.getId())
                .senderType(SenderType.MANAGER)
                .content(request.getContent())
                .messageType(request.getMessageType())
                .attachmentUrl(request.getAttachmentUrl())
                .attachmentName(request.getAttachmentName())
                .attachmentSize(request.getAttachmentSize())
                .isRead(false)
                .build();

        message = messageRepository.save(message);

        // Update conversation
        conversation.updateLastMessageTime();
        conversation.markAsUnreadForCustomer();
        conversationRepository.save(conversation);

        return convertToDto(message, manager.getId(), SenderType.MANAGER);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> getConversationMessages(UUID conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        List<Message> messages = messageRepository.findByConversationOrderByCreatedAtAsc(conversation);
        return messages.stream()
                .map(message -> convertToDto(message, null, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageDto> getConversationMessages(UUID conversationId, Pageable pageable) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        Page<Message> messages = messageRepository.findByConversationOrderByCreatedAtAsc(conversation, pageable);
        return messages.map(message -> convertToDto(message, null, null));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> getMessagesAfter(UUID conversationId, LocalDateTime after) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        List<Message> messages = messageRepository.findByConversationAndCreatedAtAfterOrderByCreatedAtAsc(conversation, after);
        return messages.stream()
                .map(message -> convertToDto(message, null, null))
                .collect(Collectors.toList());
    }

    @Override
    public void markMessageAsRead(UUID messageId, Integer userId, String userType) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        message.markAsRead();
        messageRepository.save(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> searchMessages(UUID conversationId, String searchTerm) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        List<Message> messages = messageRepository.searchByConversationAndContent(conversation, searchTerm);
        return messages.stream()
                .map(message -> convertToDto(message, null, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDto getMessageById(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        return convertToDto(message, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> getMessagesWithAttachments(UUID conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        List<Message> messages = messageRepository.findByConversationAndAttachmentUrlIsNotNullOrderByCreatedAtDesc(conversation);
        return messages.stream()
                .map(message -> convertToDto(message, null, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadMessageCount(UUID conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        return messageRepository.countByConversationAndIsReadFalse(conversation);
    }

    private MessageDto convertToDto(Message message, Integer currentUserId, SenderType currentUserType) {
        String senderName = getSenderName(message);
        boolean isMine = currentUserId != null && currentUserType != null &&
                message.getSenderId() == currentUserId.intValue() && message.getSenderType() == currentUserType;

        return MessageDto.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .senderId(message.getSenderId())
                .senderName(senderName)
                .senderType(message.getSenderType())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .attachmentUrl(message.getAttachmentUrl())
                .attachmentName(message.getAttachmentName())
                .attachmentSize(message.getAttachmentSize())
                .isRead(message.isRead())
                .readAt(message.getReadAt())
                .createdAt(message.getCreatedAt())
                .isMine(isMine)
                .build();
    }

    private String getSenderName(Message message) {
        if (message.getSenderType() == SenderType.CUSTOMER) {
            return message.getConversation().getCustomer().getFullname();
        } else if (message.getSenderType() == SenderType.MANAGER) {
            return message.getConversation().getManager() != null ?
                    message.getConversation().getManager().getFullname() : "Manager";
        }
        return "System";
    }
}
