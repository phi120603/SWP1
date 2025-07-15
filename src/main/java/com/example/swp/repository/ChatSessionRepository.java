//package com.example.swp.repository;
//
//import com.example.swp.entity.ChatSession;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//import java.util.UUID;
//
//public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {
//    Optional<ChatSession> findTopByOrderByCreatedAtAsc();
//}
//
//
