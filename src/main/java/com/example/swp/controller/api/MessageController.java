package com.example.swp.controller.api;

import com.example.swp.dto.MessageDto;
import com.example.swp.dto.SendMessageRequest;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Manager;
import com.example.swp.security.MyUserDetail;
import com.example.swp.service.ConversationService;
import com.example.swp.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversations/{conversationId}/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final ConversationService conversationService;

    @PostMapping
    public ResponseEntity<MessageDto> sendMessage(
            @PathVariable UUID conversationId,
            @Valid @RequestBody SendMessageRequest request,
            Authentication authentication) {

        MyUserDetail userDetail = (MyUserDetail) authentication.getPrincipal();
        Object user = userDetail.getUser();

        // Set conversation ID in request
        request.setConversationId(conversationId);

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

        try {
            MessageDto message;
            if (user instanceof Customer customer) {
                message = messageService.sendMessageFromCustomer(customer, request);
            } else if (user instanceof Manager manager) {
                message = messageService.sendMessageFromManager(manager, request);
            } else {
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<MessageDto>> getMessages(
            @PathVariable UUID conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size,
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

        if (page >= 0 && size > 0) {
            Pageable pageable = PageRequest.of(page, size);
            Page<MessageDto> messages = messageService.getConversationMessages(conversationId, pageable);
            return ResponseEntity.ok(messages.getContent());
        } else {
            List<MessageDto> messages = messageService.getConversationMessages(conversationId);
            return ResponseEntity.ok(messages);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<MessageDto>> getAllMessages(
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

        List<MessageDto> messages = messageService.getConversationMessages(conversationId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/after")
    public ResponseEntity<List<MessageDto>> getMessagesAfter(
            @PathVariable UUID conversationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime after,
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

        List<MessageDto> messages = messageService.getMessagesAfter(conversationId, after);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/search")
    public ResponseEntity<List<MessageDto>> searchMessages(
            @PathVariable UUID conversationId,
            @RequestParam String query,
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

        List<MessageDto> messages = messageService.searchMessages(conversationId, query);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/attachments")
    public ResponseEntity<List<MessageDto>> getMessagesWithAttachments(
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

        List<MessageDto> messages = messageService.getMessagesWithAttachments(conversationId);
        return ResponseEntity.ok(messages);
    }

    @PutMapping("/{messageId}/read")
    public ResponseEntity<Void> markMessageAsRead(
            @PathVariable UUID conversationId,
            @PathVariable UUID messageId,
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

        try {
            String userType = "";
            Integer userId = null;

            if (user instanceof Customer customer) {
                userType = "CUSTOMER";
                userId = customer.getId();
            } else if (user instanceof Manager manager) {
                userType = "MANAGER";
                userId = manager.getId();
            }

            messageService.markMessageAsRead(messageId, userId, userType);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadMessageCount(
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

        long unreadCount = messageService.getUnreadMessageCount(conversationId);
        return ResponseEntity.ok(unreadCount);
    }
}
