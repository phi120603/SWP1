package com.example.swp.service;

import com.example.swp.entity.EContract;

import java.util.List;

public interface EContractService {
    EContract createContract(EContract contract);

    List<EContract> getContractsByCustomerId(Integer customerId);

    void signContract(Integer contractId);
    EContract findById(Integer id); // THÊM HÀM NÀY

}
