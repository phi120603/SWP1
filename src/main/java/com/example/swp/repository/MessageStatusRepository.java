package com.example.swp.repository;

import com.example.swp.entity.Message;
import com.example.swp.entity.MessageStatus;
import com.example.swp.enums.MessageStatusType;
import com.example.swp.enums.SenderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageStatusRepository extends JpaRepository<MessageStatus, UUID> {

    // Find status by message and user
    Optional<MessageStatus> findByMessageAndUserIdAndUserType(Message message, Integer userId, SenderType userType);

    // Find all statuses for a message
    List<MessageStatus> findByMessage(Message message);

    // Find statuses by user
    List<MessageStatus> findByUserIdAndUserType(Integer userId, SenderType userType);

    // Find statuses by status type
    List<MessageStatus> findByStatus(MessageStatusType status);

    // Check if message is read by specific user
    boolean existsByMessageAndUserIdAndUserTypeAndStatus(Message message, Integer userId, SenderType userType, MessageStatusType status);
}
