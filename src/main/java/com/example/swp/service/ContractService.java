package com.example.swp.service;

import com.example.swp.entity.EContract;
import com.example.swp.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ContractService {
    List<EContract> getAllContracts();
    Page<EContract> getAllContracts(Pageable pageable);
    Optional<EContract> getContractById(Integer id);
    void updateContract(EContract contract);
    void updateContractStatus(Integer id, String status);
    void deleteContract(Integer id);
    EContract createContractForOrder(Order order); // Thêm mới

}
