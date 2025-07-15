package com.example.swp.service;

import com.example.swp.entity.EContract;
import com.example.swp.entity.Order;
import com.example.swp.enums.EContractStatus;

import java.util.List;
import java.util.Optional;

public interface ContractService {
    EContract createContractForOrder(Order order);
    Optional<EContract> getContractById(Long id);
    void signContract(EContract contract);
    void approveContract(EContract contract);

    List<EContract> getAllContracts();

    void updateStatus(Long contractId, EContractStatus newStatus);

    void updateContractStatus(Long id, String status);

    void updateContract(EContract contract);

    void deleteContract(Long id);
}
