package com.example.swp.service;

import com.example.swp.dto.ConversationDto;
import com.example.swp.dto.CreateConversationRequest;
import com.example.swp.dto.CreateConversationByManagerRequest;
import com.example.swp.entity.Conversation;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Manager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ConversationService {

    // Create new conversation
    ConversationDto createConversation(Customer customer, CreateConversationRequest request);

    // Create new conversation by manager
    ConversationDto createConversationByManager(Manager manager, CreateConversationByManagerRequest request);

    // Get conversation by ID
    ConversationDto getConversationById(UUID conversationId);

    // Get conversations for customer
    List<ConversationDto> getCustomerConversations(Customer customer);
    Page<ConversationDto> getCustomerConversations(Customer customer, Pageable pageable);

    // Get conversations for manager
    List<ConversationDto> getManagerConversations(Manager manager);
    Page<ConversationDto> getManagerConversations(Manager manager, Pageable pageable);

    // Get unassigned conversations
    List<ConversationDto> getUnassignedConversations();

    // Assign conversation to manager
    ConversationDto assignConversationToManager(UUID conversationId, Manager manager);

    // Mark conversation as read
    void markAsReadByCustomer(UUID conversationId, Customer customer);
    void markAsReadByManager(UUID conversationId, Manager manager);

    // Close conversation
    ConversationDto closeConversation(UUID conversationId);

    // Archive conversation
    ConversationDto archiveConversation(UUID conversationId);

    // Search conversations
    List<ConversationDto> searchManagerConversations(Manager manager, String searchTerm);

    // Get unread counts
    long getUnreadCountForCustomer(Customer customer);
    long getUnreadCountForManager(Manager manager);

    // Check if user has access to conversation
    boolean hasCustomerAccess(UUID conversationId, Customer customer);
    boolean hasManagerAccess(UUID conversationId, Manager manager);
}
