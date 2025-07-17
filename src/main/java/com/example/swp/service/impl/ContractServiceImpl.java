package com.example.swp.service.impl;

import com.example.swp.entity.EContract;
import com.example.swp.entity.Order;
import com.example.swp.enums.EContractStatus;
import com.example.swp.repository.EContractRepository;
import com.example.swp.service.ContractService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ContractServiceImpl implements ContractService {

    @Autowired
    private EContractRepository eContractRepository;

    @Override
    @Transactional
    public EContract createContractForOrder(Order order) {
        EContract contract = new EContract();
        contract.setContractCode("HD-" + UUID.randomUUID().toString().substring(0, 8));
        contract.setPricePerDay(order.getStorage().getPricePerDay());
        contract.setRentalArea(order.getRentalArea());
        contract.setTotalAmount(order.getTotalAmount());
        contract.setStorageName(order.getStorage().getStoragename());
        contract.setStatus(EContractStatus.PENDING);
        contract.setOrder(order);
        return eContractRepository.save(contract);
    }

    @Override
    public Optional<EContract> getContractById(Long id) {
        return eContractRepository.findById(id);
    }

    @Override
    public void signContract(EContract contract) {
        contract.setStatus(EContractStatus.SIGNED);
        eContractRepository.save(contract);
    }

    @Override
    public void approveContract(EContract contract) {
        contract.setStatus(EContractStatus.APPROVED);
        eContractRepository.save(contract);
    }

    @Override
    public List<EContract> getAllContracts() {
        return eContractRepository.findAll();
    }

    @Override
    public void updateStatus(Long contractId, EContractStatus newStatus) {
        EContract contract = eContractRepository.findById(contractId).orElse(null);
        if (contract != null) {
            contract.setStatus(newStatus);
            eContractRepository.save(contract);
        }
    }

    @Override
    public void updateContractStatus(Long id, String status) {
        EContract contract = eContractRepository.findById(id).orElse(null);
        if (contract != null) {
            contract.setStatus(EContractStatus.valueOf(status));
            eContractRepository.save(contract);
        }
    }

    @Override
    public void updateContract(EContract contract) {
        eContractRepository.save(contract);
    }

    @Override
    public void deleteContract(Long id) {
        eContractRepository.deleteById(id);
    }
}