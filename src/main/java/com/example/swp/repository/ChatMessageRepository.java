package com.example.swp.repository;

import com.example.swp.dto.ChatMessageResponse;
import com.example.swp.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    List<ChatMessage> findByRoomIdOrderByCreatedAtAsc(String roomId);

    @Query("SELECT DISTINCT c.roomId FROM ChatMessage c ORDER BY MAX(c.createdAt) DESC")
    List<String> findAllDistinctRoomIds();

//    // ✅ Tìm tất cả tin nhắn gửi đến một receiver nhất định (ví dụ Manager)
//    @Query("SELECT m FROM ChatMessage m WHERE m.roomId = :receiverId ORDER BY m.createdAt ASC")
//    List<ChatMessage> findAllByReceiverId(@Param("receiverId") String receiverId);


}

