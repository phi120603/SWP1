package com.example.swp.repository;

import com.example.swp.entity.Conversation;
import com.example.swp.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    // Find messages by conversation ordered by creation time
    List<Message> findByConversationOrderByCreatedAtAsc(Conversation conversation);

    Page<Message> findByConversationOrderByCreatedAtDesc(Conversation conversation, Pageable pageable);
    Page<Message> findByConversationOrderByCreatedAtAsc(Conversation conversation, Pageable pageable);

    // Find unread messages in a conversation
    List<Message> findByConversationAndIsReadFalseOrderByCreatedAtAsc(Conversation conversation);

    // Count unread messages in a conversation
    long countByConversationAndIsReadFalse(Conversation conversation);

    // Find latest message in a conversation
    @Query("SELECT m FROM Message m WHERE m.conversation = :conversation ORDER BY m.createdAt DESC LIMIT 1")
    Message findLatestByConversation(@Param("conversation") Conversation conversation);

    // Find messages after a specific time (for real-time updates)
    List<Message> findByConversationAndCreatedAtAfterOrderByCreatedAtAsc(Conversation conversation, LocalDateTime after);

    // Search messages by content
    @Query("SELECT m FROM Message m WHERE m.conversation = :conversation AND " +
            "LOWER(m.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "ORDER BY m.createdAt DESC")
    List<Message> searchByConversationAndContent(@Param("conversation") Conversation conversation,
                                                 @Param("searchTerm") String searchTerm);

    // Count total messages in conversation
    long countByConversation(Conversation conversation);

    // Find messages with attachments
    List<Message> findByConversationAndAttachmentUrlIsNotNullOrderByCreatedAtDesc(Conversation conversation);
}
