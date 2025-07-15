package com.example.swp.repository;

import com.example.swp.entity.Conversation;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Manager;
import com.example.swp.enums.ConversationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    // Find conversations by customer
    List<Conversation> findByCustomerOrderByLastMessageAtDesc(Customer customer);

    Page<Conversation> findByCustomerOrderByLastMessageAtDesc(Customer customer, Pageable pageable);

    // Find conversations by manager
    List<Conversation> findByManagerOrderByLastMessageAtDesc(Manager manager);

    Page<Conversation> findByManagerOrderByLastMessageAtDesc(Manager manager, Pageable pageable);

    // Find active conversation between customer and manager
    Optional<Conversation> findByCustomerAndManagerAndStatus(Customer customer, Manager manager, ConversationStatus status);

    // Find conversations by status
    List<Conversation> findByStatusOrderByLastMessageAtDesc(ConversationStatus status);

    // Find unassigned conversations (pending assignment)
    List<Conversation> findByManagerIsNullAndStatusOrderByCreatedAtAsc(ConversationStatus status);

    // Count unread conversations for customer
    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.customer = :customer AND c.customerUnread = true")
    long countUnreadByCustomer(@Param("customer") Customer customer);

    // Count unread conversations for manager
    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.manager = :manager AND c.managerUnread = true")
    long countUnreadByManager(@Param("manager") Manager manager);

    // Find conversations with unread messages for manager
    List<Conversation> findByManagerAndManagerUnreadTrueOrderByLastMessageAtDesc(Manager manager);

    // Find conversations with unread messages for customer
    List<Conversation> findByCustomerAndCustomerUnreadTrueOrderByLastMessageAtDesc(Customer customer);

    // Search conversations by customer name for manager
    @Query("SELECT c FROM Conversation c WHERE c.manager = :manager AND " +
            "LOWER(c.customer.fullname) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "ORDER BY c.lastMessageAt DESC")
    List<Conversation> searchByManagerAndCustomerName(@Param("manager") Manager manager,
                                                      @Param("searchTerm") String searchTerm);
}
