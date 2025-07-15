package com.example.swp.repository;

import com.example.swp.entity.Issue;
import com.example.swp.enums.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Integer> {
    List<Issue> findByCustomerId(int customerId);
    List<Issue> findByStatus(IssueStatus status);
    long countByStatus(IssueStatus status);
    long countByCustomerId(int customerId);
    long countByAssignedStaff_Staffid(int staffid);

    // ====== THÊM HAI HÀM NÀY ĐỂ SEARCH VÀ LỌC ======
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
    List<Issue> searchByKeywordAndStatus(@Param("keyword") String keyword, @Param("status") IssueStatus status);
}
