package com.example.swp.controller.api;

import com.example.swp.dto.ConversationDto;
import com.example.swp.dto.CreateConversationByManagerRequest;
import com.example.swp.dto.CreateConversationRequest;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Manager;
import com.example.swp.security.MyUserDetail;
import com.example.swp.service.ConversationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @PostMapping
    public ResponseEntity<ConversationDto> createConversation(
            @Valid @RequestBody CreateConversationRequest request,
            Authentication authentication) {

        MyUserDetail userDetail = (MyUserDetail) authentication.getPrincipal();

        if (!(userDetail.getUser() instanceof Customer customer)) {
            return ResponseEntity.badRequest().build();
        }

        ConversationDto conversation = conversationService.createConversation(customer, request);
        return ResponseEntity.ok(conversation);
    }

    @GetMapping
    public ResponseEntity<List<ConversationDto>> getConversations(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        MyUserDetail userDetail = (MyUserDetail) authentication.getPrincipal();
        Object user = userDetail.getUser();

        if (user instanceof Customer customer) {
            if (page >= 0 && size > 0) {
                Pageable pageable = PageRequest.of(page, size);
                Page<ConversationDto> conversations = conversationService.getCustomerConversations(customer, pageable);
                return ResponseEntity.ok(conversations.getContent());
            } else {
                List<ConversationDto> conversations = conversationService.getCustomerConversations(customer);
                return ResponseEntity.ok(conversations);
            }
        } else if (user instanceof Manager manager) {
            if (page >= 0 && size > 0) {
                Pageable pageable = PageRequest.of(page, size);
                Page<ConversationDto> conversations = conversationService.getManagerConversations(manager, pageable);
                return ResponseEntity.ok(conversations.getContent());
            } else {
                List<ConversationDto> conversations = conversationService.getManagerConversations(manager);
                return ResponseEntity.ok(conversations);
            }
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<ConversationDto> getConversation(
            @PathVariable UUID conversationId,
            Authentication authentication) {

        MyUserDetail userDetail = (MyUserDetail) authentication.getPrincipal();
        Object user = userDetail.getUser();

        // Check access permissions
        boolean hasAccess = false;
        if (user instanceof Customer customer) {
            hasAccess = conversationService.hasCustomerAccess(conversationId, customer);
        } else if (user instanceof Manager manager) {
            hasAccess = conversationService.hasManagerAccess(conversationId, manager);
        }

        if (!hasAccess) {
            return ResponseEntity.status(403).build();
        }

        ConversationDto conversation = conversationService.getConversationById(conversationId);
        return ResponseEntity.ok(conversation);
    }

    @PutMapping("/{conversationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable UUID conversationId,
            Authentication authentication) {

        MyUserDetail userDetail = (MyUserDetail) authentication.getPrincipal();
        Object user = userDetail.getUser();

        try {
            if (user instanceof Customer customer) {
                conversationService.markAsReadByCustomer(conversationId, customer);
            } else if (user instanceof Manager manager) {
                conversationService.markAsReadByManager(conversationId, manager);
            } else {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{conversationId}/assign")
    public ResponseEntity<ConversationDto> assignToManager(
            @PathVariable UUID conversationId,
            Authentication authentication) {

        MyUserDetail userDetail = (MyUserDetail) authentication.getPrincipal();

        if (!(userDetail.getUser() instanceof Manager manager)) {
            return ResponseEntity.status(403).build();
        }

        try {
            ConversationDto conversation = conversationService.assignConversationToManager(conversationId, manager);
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{conversationId}/close")
    public ResponseEntity<ConversationDto> closeConversation(
            @PathVariable UUID conversationId,
            Authentication authentication) {

        MyUserDetail userDetail = (MyUserDetail) authentication.getPrincipal();

        if (!(userDetail.getUser() instanceof Manager)) {
            return ResponseEntity.status(403).build();
        }

        try {
            ConversationDto conversation = conversationService.closeConversation(conversationId);
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/unassigned")
    public ResponseEntity<List<ConversationDto>> getUnassignedConversations(Authentication authentication) {
        MyUserDetail userDetail = (MyUserDetail) authentication.getPrincipal();

        if (!(userDetail.getUser() instanceof Manager)) {
            return ResponseEntity.status(403).build();
        }

        List<ConversationDto> conversations = conversationService.getUnassignedConversations();
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ConversationDto>> searchConversations(
            @RequestParam String query,
            Authentication authentication) {

        MyUserDetail userDetail = (MyUserDetail) authentication.getPrincipal();

        if (!(userDetail.getUser() instanceof Manager manager)) {
            return ResponseEntity.status(403).build();
        }

        List<ConversationDto> conversations = conversationService.searchManagerConversations(manager, query);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(Authentication authentication) {
        MyUserDetail userDetail = (MyUserDetail) authentication.getPrincipal();
        Object user = userDetail.getUser();

        long unreadCount = 0;
        if (user instanceof Customer customer) {
            unreadCount = conversationService.getUnreadCountForCustomer(customer);
        } else if (user instanceof Manager manager) {
            unreadCount = conversationService.getUnreadCountForManager(manager);
        }

        return ResponseEntity.ok(unreadCount);
    }

    @PostMapping("/manager-create")
    public ResponseEntity<ConversationDto> createConversationByManager(
            @Valid @RequestBody CreateConversationByManagerRequest request,
            Authentication authentication) {

        MyUserDetail userDetail = (MyUserDetail) authentication.getPrincipal();

        if (!(userDetail.getUser() instanceof Manager manager)) {
            return ResponseEntity.status(403).build();
        }

        try {
            ConversationDto conversation = conversationService.createConversationByManager(manager, request);
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
