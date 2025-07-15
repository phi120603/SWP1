package com.example.swp.service.impl;

import com.example.swp.dto.ConversationDto;
import com.example.swp.dto.CreateConversationRequest;
import com.example.swp.dto.CreateConversationByManagerRequest;
import com.example.swp.entity.Conversation;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Manager;
import com.example.swp.repository.CustomerRepository;
import com.example.swp.entity.Message;
import com.example.swp.enums.ConversationStatus;
import com.example.swp.enums.MessageType;
import com.example.swp.enums.SenderType;
import com.example.swp.repository.ConversationRepository;
import com.example.swp.repository.ManageRepository;
import com.example.swp.repository.MessageRepository;
import com.example.swp.service.ConversationService;
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
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ManageRepository managerRepository;
    private final CustomerRepository customerRepository;

    @Override
    public ConversationDto createConversation(Customer customer, CreateConversationRequest request) {
        Manager manager = managerRepository.findById(request.getManagerId())
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        // Check if active conversation already exists
        var existingConversation = conversationRepository
                .findByCustomerAndManagerAndStatus(customer, manager, ConversationStatus.ACTIVE);

        if (existingConversation.isPresent()) {
            return convertToDto(existingConversation.get());
        }

        // Create new conversation
        Conversation conversation = Conversation.builder()
                .customer(customer)
                .manager(manager)
                .status(ConversationStatus.ACTIVE)
                .customerUnread(false)
                .managerUnread(true)
                .lastMessageAt(LocalDateTime.now())
                .build();

        conversation = conversationRepository.save(conversation);

        // Create initial message
        Message initialMessage = Message.builder()
                .conversation(conversation)
                .senderId(customer.getId())
                .senderType(SenderType.CUSTOMER)
                .content(request.getInitialMessage())
                .messageType(MessageType.TEXT)
                .isRead(false)
                .build();

        messageRepository.save(initialMessage);

        return convertToDto(conversation);
    }

    @Override
    public ConversationDto createConversationByManager(Manager manager, CreateConversationByManagerRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Check if active conversation already exists
        var existingConversation = conversationRepository
                .findByCustomerAndManagerAndStatus(customer, manager, ConversationStatus.ACTIVE);

        if (existingConversation.isPresent()) {
            return convertToDto(existingConversation.get());
        }

        // Create new conversation
        Conversation conversation = Conversation.builder()
                .customer(customer)
                .manager(manager)
                .status(ConversationStatus.ACTIVE)
                .customerUnread(true)
                .managerUnread(false)
                .lastMessageAt(LocalDateTime.now())
                .build();

        conversation = conversationRepository.save(conversation);

        // Create initial message from manager
        Message initialMessage = Message.builder()
                .conversation(conversation)
                .senderId(manager.getId())
                .senderType(SenderType.MANAGER)
                .content(request.getInitialMessage())
                .messageType(MessageType.TEXT)
                .isRead(false)
                .build();

        messageRepository.save(initialMessage);

        return convertToDto(conversation);
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationDto getConversationById(UUID conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        return convertToDto(conversation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationDto> getCustomerConversations(Customer customer) {
        List<Conversation> conversations = conversationRepository.findByCustomerOrderByLastMessageAtDesc(customer);
        return conversations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConversationDto> getCustomerConversations(Customer customer, Pageable pageable) {
        Page<Conversation> conversations = conversationRepository.findByCustomerOrderByLastMessageAtDesc(customer, pageable);
        return conversations.map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationDto> getManagerConversations(Manager manager) {
        List<Conversation> conversations = conversationRepository.findByManagerOrderByLastMessageAtDesc(manager);
        return conversations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConversationDto> getManagerConversations(Manager manager, Pageable pageable) {
        Page<Conversation> conversations = conversationRepository.findByManagerOrderByLastMessageAtDesc(manager, pageable);
        return conversations.map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationDto> getUnassignedConversations() {
        List<Conversation> conversations = conversationRepository
                .findByManagerIsNullAndStatusOrderByCreatedAtAsc(ConversationStatus.PENDING_ASSIGNMENT);
        return conversations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ConversationDto assignConversationToManager(UUID conversationId, Manager manager) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        conversation.setManager(manager);
        conversation.setStatus(ConversationStatus.ACTIVE);
        conversation.setManagerUnread(true);

        conversation = conversationRepository.save(conversation);
        return convertToDto(conversation);
    }

    @Override
    public void markAsReadByCustomer(UUID conversationId, Customer customer) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        if (conversation.getCustomer().getId() != customer.getId()) {
            throw new RuntimeException("Access denied");
        }

        conversation.markAsReadByCustomer();
        conversationRepository.save(conversation);
    }

    @Override
    public void markAsReadByManager(UUID conversationId, Manager manager) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        if (conversation.getManager() == null || conversation.getManager().getId() != manager.getId()) {
            throw new RuntimeException("Access denied");
        }

        conversation.markAsReadByManager();
        conversationRepository.save(conversation);
    }

    @Override
    public ConversationDto closeConversation(UUID conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        conversation.setStatus(ConversationStatus.CLOSED);
        conversation = conversationRepository.save(conversation);
        return convertToDto(conversation);
    }

    @Override
    public ConversationDto archiveConversation(UUID conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        conversation.setStatus(ConversationStatus.ARCHIVED);
        conversation = conversationRepository.save(conversation);
        return convertToDto(conversation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationDto> searchManagerConversations(Manager manager, String searchTerm) {
        List<Conversation> conversations = conversationRepository
                .searchByManagerAndCustomerName(manager, searchTerm);
        return conversations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCountForCustomer(Customer customer) {
        return conversationRepository.countUnreadByCustomer(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCountForManager(Manager manager) {
        return conversationRepository.countUnreadByManager(manager);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasCustomerAccess(UUID conversationId, Customer customer) {
        return conversationRepository.findById(conversationId)
                .map(conv -> conv.getCustomer().getId() == customer.getId())
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasManagerAccess(UUID conversationId, Manager manager) {
        return conversationRepository.findById(conversationId)
                .map(conv -> conv.getManager() != null && conv.getManager().getId() == manager.getId())
                .orElse(false);
    }

    private ConversationDto convertToDto(Conversation conversation) {
        Message lastMessage = messageRepository.findLatestByConversation(conversation);
        long unreadCount = messageRepository.countByConversationAndIsReadFalse(conversation);

        return ConversationDto.builder()
                .id(conversation.getId())
                .customerId(conversation.getCustomer().getId())
                .customerName(conversation.getCustomer().getFullname())
                .customerEmail(conversation.getCustomer().getEmail())
                .managerId(conversation.getManager() != null ? conversation.getManager().getId() : null)
                .managerName(conversation.getManager() != null ? conversation.getManager().getFullname() : null)
                .managerEmail(conversation.getManager() != null ? conversation.getManager().getEmail() : null)
                .status(conversation.getStatus())
                .customerUnread(conversation.isCustomerUnread())
                .managerUnread(conversation.isManagerUnread())
                .lastMessage(lastMessage != null ? lastMessage.getContent() : null)
                .lastMessageAt(conversation.getLastMessageAt())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .unreadCount(unreadCount)
                .isOnline(conversation.getCustomer().getIsOnline() != null ? conversation.getCustomer().getIsOnline() : false)
                .build();
    }
}
