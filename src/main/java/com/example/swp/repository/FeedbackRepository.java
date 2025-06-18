package com.example.swp.repository;
import com.example.swp.entity.Customer;
import com.example.swp.entity.Feedback;
import com.example.swp.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    boolean existsByCustomerAndStorage(Customer customer, Storage storage);
    Optional<Feedback> findByCustomerAndStorage(Customer customer, Storage storage);


}
