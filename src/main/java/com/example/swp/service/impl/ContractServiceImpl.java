package com.example.swp.service.impl;

import com.example.swp.entity.EContract;
import com.example.swp.entity.Order;
import com.example.swp.enums.EContractStatus;
import com.example.swp.repository.EContractRepository;
import com.example.swp.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContractServiceImpl implements ContractService {

    @Autowired
    private EContractRepository eContractRepository;

    @Override
    public List<EContract> getAllContracts() {
        return eContractRepository.findAll();
    }

    @Override
    public Page<EContract> getAllContracts(Pageable pageable) {
        return eContractRepository.findAll(pageable);
    }

    @Override
    public Optional<EContract> getContractById(Integer id) {
        return eContractRepository.findById(id);
    }

    @Override
    public void updateContract(EContract contract) {
        eContractRepository.save(contract);
    }

    @Override
    public void updateContractStatus(Integer id, String status) {
        Optional<EContract> optional = eContractRepository.findById(id);
        if (optional.isPresent()) {
            EContract contract = optional.get();
            contract.setStatus(EContractStatus.valueOf(status));
            eContractRepository.save(contract);
        }
    }

    @Override
    public void deleteContract(Integer id) {
        eContractRepository.deleteById(id);
    }

    @Override
    public EContract createContractForOrder(Order order) {
        EContract contract = new EContract();
        contract.setOrder(order);
        return eContractRepository.save(contract);
    }
}
