package com.example.swp.controller.websocket;

import com.example.swp.dto.MessageDto;
import com.example.swp.dto.SendMessageRequest;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Manager;
import com.example.swp.enums.SenderType;
import com.example.swp.repository.CustomerRepository;
import com.example.swp.repository.ManageRepository;
import com.example.swp.security.MyUserDetail;
import com.example.swp.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MessengerWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final CustomerRepository customerRepository;
    private final ManageRepository managerRepository;

    @MessageMapping("/message.send")
    public void sendMessage(@Payload SendMessageRequest request, Principal principal) {
        System.out.println("🔔 WebSocket message received: " + request);
        System.out.println("🔍 Principal: " + principal);
        System.out.println("🔍 Principal name: " + (principal != null ? principal.getName() : "null"));

        try {
            Authentication auth = (Authentication) principal;
            MyUserDetail userDetail = (MyUserDetail) auth.getPrincipal();
            Object user = userDetail.getUser();

            System.out.println("🔍 User type: " + user.getClass().getSimpleName());

            MessageDto savedMessage;

            if (user instanceof Customer customer) {
                System.out.println("📤 Processing customer message...");
                savedMessage = messageService.sendMessageFromCustomer(customer, request);
                System.out.println("✅ Message saved: " + savedMessage.getId());

                // Send confirmation back to customer who sent it
                System.out.println("📨 Sending confirmation to customer: " + principal.getName());
                messagingTemplate.convertAndSendToUser(
                        principal.getName(),
                        "/queue/notifications",
                        savedMessage
                );

                // Send to all managers (not back to customer who sent it)
                System.out.println("📢 Broadcasting to managers: /topic/manager/notifications");
                messagingTemplate.convertAndSend("/topic/manager/notifications", savedMessage);

            } else if (user instanceof Manager manager) {
                System.out.println("📤 Processing manager message...");
                savedMessage = messageService.sendMessageFromManager(manager, request);
                System.out.println("✅ Message saved: " + savedMessage.getId());

                // Send confirmation back to manager who sent it
                System.out.println("📨 Sending confirmation to manager: " + principal.getName());
                messagingTemplate.convertAndSendToUser(
                        principal.getName(),
                        "/queue/notifications",
                        savedMessage
                );

                // Send to all customers (not back to manager who sent it)
                System.out.println("📢 Broadcasting to customers: /topic/customer/notifications");
                messagingTemplate.convertAndSend("/topic/customer/notifications", savedMessage);
            }

            System.out.println("🎉 WebSocket message processing completed successfully");

        } catch (Exception e) {
            System.err.println("❌ WebSocket message processing error: " + e.getMessage());
            e.printStackTrace();

            // Send error back to sender
            try {
                messagingTemplate.convertAndSendToUser(
                        principal.getName(),
                        "/queue/errors",
                        "Error processing message: " + e.getMessage()
                );
            } catch (Exception sendError) {
                System.err.println("❌ Failed to send error message: " + sendError.getMessage());
            }
            log.error("Error sending message: ", e);
            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/errors",
                    "Failed to send message: " + e.getMessage()
            );
        }
    }

    @MessageMapping("/message.read/{conversationId}")
    public void markAsRead(@DestinationVariable UUID conversationId, Principal principal) {
        try {
            Authentication auth = (Authentication) principal;
            MyUserDetail userDetail = (MyUserDetail) auth.getPrincipal();
            Object user = userDetail.getUser();

            if (user instanceof Customer customer) {
                // Mark conversation as read by customer
                // Implementation would mark all unread messages in conversation as read
                messagingTemplate.convertAndSend(
                        "/topic/conversation/" + conversationId + "/read",
                        new ReadReceiptEvent(customer.getId(), SenderType.CUSTOMER)
                );

            } else if (user instanceof Manager manager) {
                // Mark conversation as read by manager
                messagingTemplate.convertAndSend(
                        "/topic/conversation/" + conversationId + "/read",
                        new ReadReceiptEvent(manager.getId(), SenderType.MANAGER)
                );
            }

        } catch (Exception e) {
            log.error("Error marking messages as read: ", e);
        }
    }

    @MessageMapping("/typing.start/{conversationId}")
    public void startTyping(@DestinationVariable UUID conversationId, Principal principal) {
        try {
            Authentication auth = (Authentication) principal;
            MyUserDetail userDetail = (MyUserDetail) auth.getPrincipal();
            Object user = userDetail.getUser();

            String userName = "";
            SenderType senderType = null;

            if (user instanceof Customer customer) {
                userName = customer.getFullname();
                senderType = SenderType.CUSTOMER;
            } else if (user instanceof Manager manager) {
                userName = manager.getFullname();
                senderType = SenderType.MANAGER;
            }

            TypingEvent typingEvent = new TypingEvent(userName, senderType, true);

            messagingTemplate.convertAndSend(
                    "/topic/conversation/" + conversationId + "/typing",
                    typingEvent
            );

        } catch (Exception e) {
            log.error("Error handling typing start: ", e);
        }
    }

    @MessageMapping("/typing.stop/{conversationId}")
    public void stopTyping(@DestinationVariable UUID conversationId, Principal principal) {
        try {
            Authentication auth = (Authentication) principal;
            MyUserDetail userDetail = (MyUserDetail) auth.getPrincipal();
            Object user = userDetail.getUser();

            String userName = "";
            SenderType senderType = null;

            if (user instanceof Customer customer) {
                userName = customer.getFullname();
                senderType = SenderType.CUSTOMER;
            } else if (user instanceof Manager manager) {
                userName = manager.getFullname();
                senderType = SenderType.MANAGER;
            }

            TypingEvent typingEvent = new TypingEvent(userName, senderType, false);

            messagingTemplate.convertAndSend(
                    "/topic/conversation/" + conversationId + "/typing",
                    typingEvent
            );

        } catch (Exception e) {
            log.error("Error handling typing stop: ", e);
        }
    }

    @MessageMapping("/user.online")
    public void userOnline(Principal principal) {
        try {
            Authentication auth = (Authentication) principal;
            MyUserDetail userDetail = (MyUserDetail) auth.getPrincipal();
            Object user = userDetail.getUser();

            if (user instanceof Customer customer) {
                customer.setIsOnline(true);
                customerRepository.save(customer);

                messagingTemplate.convertAndSend(
                        "/topic/user.status",
                        new UserStatusEvent(customer.getId(), SenderType.CUSTOMER, true)
                );

            } else if (user instanceof Manager manager) {
                manager.setIsOnline(true);
                managerRepository.save(manager);

                messagingTemplate.convertAndSend(
                        "/topic/user.status",
                        new UserStatusEvent(manager.getId(), SenderType.MANAGER, true)
                );
            }

        } catch (Exception e) {
            log.error("Error updating user online status: ", e);
        }
    }

    @MessageMapping("/user.offline")
    public void userOffline(Principal principal) {
        try {
            Authentication auth = (Authentication) principal;
            MyUserDetail userDetail = (MyUserDetail) auth.getPrincipal();
            Object user = userDetail.getUser();

            if (user instanceof Customer customer) {
                customer.setIsOnline(false);
                customer.setLastSeen(java.time.LocalDateTime.now());
                customerRepository.save(customer);

                messagingTemplate.convertAndSend(
                        "/topic/user.status",
                        new UserStatusEvent(customer.getId(), SenderType.CUSTOMER, false)
                );

            } else if (user instanceof Manager manager) {
                manager.setIsOnline(false);
                manager.setLastSeen(java.time.LocalDateTime.now());
                managerRepository.save(manager);

                messagingTemplate.convertAndSend(
                        "/topic/user.status",
                        new UserStatusEvent(manager.getId(), SenderType.MANAGER, false)
                );
            }

        } catch (Exception e) {
            log.error("Error updating user offline status: ", e);
        }
    }

    // Event classes
    public static class ReadReceiptEvent {
        public Integer userId;
        public SenderType userType;

        public ReadReceiptEvent(Integer userId, SenderType userType) {
            this.userId = userId;
            this.userType = userType;
        }
    }

    public static class TypingEvent {
        public String userName;
        public SenderType userType;
        public boolean isTyping;

        public TypingEvent(String userName, SenderType userType, boolean isTyping) {
            this.userName = userName;
            this.userType = userType;
            this.isTyping = isTyping;
        }
    }

    public static class UserStatusEvent {
        public Integer userId;
        public SenderType userType;
        public boolean isOnline;

        public UserStatusEvent(Integer userId, SenderType userType, boolean isOnline) {
            this.userId = userId;
            this.userType = userType;
            this.isOnline = isOnline;
        }
    }
}
