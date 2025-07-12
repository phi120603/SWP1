package com.example.swp.service.impl;

import com.example.swp.entity.EContract;
import com.example.swp.repository.EContractRepository;
import com.example.swp.service.EContractService;
import com.example.swp.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EContractServiceImpl implements EContractService {

    private final EContractRepository contractRepo;
    private final EmailService emailService;

    @Override
    public EContract createContract(EContract contract) {
        contract.setCreatedDate(LocalDate.now());
        contract.setSigned(false);
        return contractRepo.save(contract);
    }

    @Override
    public List<EContract> getContractsByCustomerId(Integer customerId) {
        return contractRepo.findByCustomerId(customerId);
    }

    @Override
    @Transactional
    public void signContract(Integer contractId) {
        EContract contract = contractRepo.findById(contractId).orElseThrow();
        contract.setSigned(true);
        contractRepo.save(contract);

        // Gửi email sau khi ký hợp đồng thành công
        emailService.sendPaymentEmail(contract.getCustomer().getEmail(), contract.getId());
    }
}
