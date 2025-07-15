package com.example.swp.service.impl;

import com.example.swp.entity.EContract;
import com.example.swp.repository.EContractRepository;
import com.example.swp.service.EContractService;
import com.example.swp.service.EmailService;
import com.example.swp.util.PDFGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EContractServiceImpl implements EContractService {

    private final EContractRepository contractRepo;
    private final EmailService emailService;
    private static final Logger log = LoggerFactory.getLogger(EContractServiceImpl.class);

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
    public EContract findById(Integer id) {
        return contractRepo.findById(id)
                .orElse(null);
    }

    @Override
    @Transactional
    public void signContract(Integer contractId) {
        EContract contract = contractRepo.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found with ID: " + contractId));

        contract.setSigned(true);
        contractRepo.save(contract);

        String pdfPath;
        try {
            pdfPath = PDFGenerator.generateContractPdf(contract);
        } catch (Exception e) {
            log.error("Lỗi khi tạo PDF hợp đồng ID={}", contractId, e);
            pdfPath = null;
        }

        emailService.sendPaymentEmail(
                contract.getCustomer().getEmail(),
                contractId,
                pdfPath
        );
    }
}
