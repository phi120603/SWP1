package com.example.swp.repository;

import com.example.swp.entity.Issue;
import com.example.swp.enums.IssueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Integer> {

    // Các hàm sẵn có
    List<Issue> findByCustomerId(int customerId);
    List<Issue> findByStatus(IssueStatus status);
    long countByStatus(IssueStatus status);
    long countByCustomerId(int customerId);
    long countByAssignedStaff_Staffid(int staffid);

    // ==============================
    // 🔍 TÌM KIẾM KHÔNG PHÂN TRANG
    // ==============================
    @Query("""
        SELECT i FROM Issue i 
        WHERE 
            (:keyword IS NULL OR :keyword = '' OR 
                LOWER(i.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
                LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
                LOWER(i.customer.fullname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
                LOWER(i.assignedStaff.fullname) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
    """)
    List<Issue> searchByKeyword(@Param("keyword") String keyword);

    @Query("""
        SELECT i FROM Issue i 
        WHERE 
            (:keyword IS NULL OR :keyword = '' OR 
                LOWER(i.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
                LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
                LOWER(i.customer.fullname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
                LOWER(i.assignedStaff.fullname) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            AND (:status IS NULL OR i.status = :status)
    """)
    List<Issue> searchByKeywordAndStatus(@Param("keyword") String keyword,
                                         @Param("status") IssueStatus status);


    // ==========================================
    // 📄 PHÂN TRANG CHO TÌM KIẾM + LỌC
    // ==========================================

    @Query("""
        SELECT i FROM Issue i 
        WHERE 
            (:keyword IS NULL OR :keyword = '' OR 
                LOWER(i.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
                LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
                LOWER(i.customer.fullname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
                LOWER(i.assignedStaff.fullname) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            AND (:status IS NULL OR i.status = :status)
    """)
    Page<Issue> searchByKeywordAndStatus(@Param("keyword") String keyword,
                                         @Param("status") IssueStatus status,
                                         Pageable pageable);

    @Query("""
        SELECT i FROM Issue i 
        WHERE 
            (:keyword IS NULL OR :keyword = '' OR 
                LOWER(i.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
                LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
                LOWER(i.customer.fullname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
                LOWER(i.assignedStaff.fullname) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
    """)
    Page<Issue> searchByKeyword(@Param("keyword") String keyword,
                                Pageable pageable);

    Page<Issue> findByStatus(IssueStatus status, Pageable pageable);

    // Dùng cho khách hàng
    Page<Issue> findByCustomerId(int customerId, Pageable pageable);
    Page<Issue> findByCustomerIdAndStatus(int customerId, IssueStatus status, Pageable pageable);
}
