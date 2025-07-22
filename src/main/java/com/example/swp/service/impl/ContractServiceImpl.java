package com.example.swp.service.impl;

import com.example.swp.entity.EContract;
import com.example.swp.entity.Order;
import com.example.swp.enums.EContractStatus;
import com.example.swp.repository.EContractRepository;
import com.example.swp.service.ContractService;
import com.example.swp.service.ActivityLogService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ContractServiceImpl implements ContractService {

    @Autowired
    private EContractRepository eContractRepository;

    @Autowired
    private ActivityLogService activityLogService;

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

        EContract savedContract = eContractRepository.save(contract);

        // Ghi log tạo hợp đồng
        activityLogService.logActivity(
                "Tạo hợp đồng điện tử",
                "Hệ thống tạo hợp đồng điện tử " + savedContract.getContractCode()
                        + " cho kho " + savedContract.getStorageName()
                        + " - Diện tích thuê: " + savedContract.getRentalArea() + "m²"
                        + " - Giá thuê: " + savedContract.getPricePerDay() + "đ/ngày"
                        + " - Tổng tiền: " + savedContract.getTotalAmount() + "đ"
                        + " - Trạng thái: Chờ xử lý",
                order.getCustomer(),
                order, null, null, null, null
        );

        return savedContract;
    }

    @Override
    public Optional<EContract> getContractById(Long id) {
        return eContractRepository.findById(id);
    }

    @Override
    public void signContract(EContract contract) {
        EContractStatus oldStatus = contract.getStatus();
        contract.setStatus(EContractStatus.SIGNED);
        EContract updatedContract = eContractRepository.save(contract);

        // Ghi log ký hợp đồng
        activityLogService.logActivity(
                "Ký hợp đồng điện tử",
                "Khách hàng " + updatedContract.getOrder().getCustomer().getFullname()
                        + " đã ký hợp đồng " + updatedContract.getContractCode()
                        + " - Trạng thái: " + oldStatus + " → " + EContractStatus.SIGNED,
                updatedContract.getOrder().getCustomer(),
                updatedContract.getOrder(), null, null, null, null
        );
    }

    @Override
    public void approveContract(EContract contract) {
        EContractStatus oldStatus = contract.getStatus();
        contract.setStatus(EContractStatus.APPROVED);
        EContract updatedContract = eContractRepository.save(contract);

        // Ghi log phê duyệt hợp đồng
        activityLogService.logActivity(
                "Hợp đồng được phê duyệt",
                "Hợp đồng " + updatedContract.getContractCode() + " đã được phê duyệt"
                        + " - Trạng thái: " + oldStatus + " → " + EContractStatus.APPROVED
                        + " - Khách hàng có thể bắt đầu sử dụng dịch vụ",
                updatedContract.getOrder().getCustomer(),
                updatedContract.getOrder(), null, null, null, null
        );
    }

    @Override
    public List<EContract> getAllContracts() {
        return eContractRepository.findAll();
    }

    @Override
    public void updateStatus(Long contractId, EContractStatus newStatus) {
        EContract contract = eContractRepository.findById(contractId).orElse(null);
        if (contract != null) {
            EContractStatus oldStatus = contract.getStatus();
            contract.setStatus(newStatus);
            EContract updatedContract = eContractRepository.save(contract);

            // Ghi log cập nhật trạng thái
            activityLogService.logActivity(
                    "Cập nhật trạng thái hợp đồng",
                    "Trạng thái hợp đồng " + updatedContract.getContractCode()
                            + " được cập nhật: " + oldStatus + " → " + newStatus,
                    updatedContract.getOrder().getCustomer(),
                    updatedContract.getOrder(), null, null, null, null
            );
        }
    }

    @Override
    public void updateContractStatus(Long id, String status) {
        EContract contract = eContractRepository.findById(id).orElse(null);
        if (contract != null) {
            EContractStatus oldStatus = contract.getStatus();
            EContractStatus newStatus = EContractStatus.valueOf(status);
            contract.setStatus(newStatus);
            EContract updatedContract = eContractRepository.save(contract);

            // Ghi log cập nhật trạng thái
            activityLogService.logActivity(
                    "Cập nhật trạng thái hợp đồng",
                    "Trạng thái hợp đồng " + updatedContract.getContractCode()
                            + " được cập nhật: " + oldStatus + " → " + newStatus,
                    updatedContract.getOrder().getCustomer(),
                    updatedContract.getOrder(), null, null, null, null
            );
        }
    }

    @Override
    public void updateContract(EContract contract) {
        // Ghi log cập nhật hợp đồng
        activityLogService.logActivity(
                "Cập nhật hợp đồng",
                "Hợp đồng " + contract.getContractCode() + " đã được cập nhật thông tin"
                        + " - Tổng tiền: " + contract.getTotalAmount() + "đ"
                        + " - Trạng thái: " + contract.getStatus(),
                contract.getOrder().getCustomer(),
                contract.getOrder(), null, null, null, null
        );

        eContractRepository.save(contract);
    }

    @Override
    public Page<EContract> getContractsPage(Pageable pageable) {
        return eContractRepository.findAll(pageable);
    }



    @Override
    public void deleteContract(Long id) {
        Optional<EContract> contractOpt = eContractRepository.findById(id);
        if (contractOpt.isPresent()) {
            EContract contract = contractOpt.get();

            // Ghi log xóa hợp đồng
            activityLogService.logActivity(
                    "Hủy hợp đồng",
                    "Hợp đồng " + contract.getContractCode() + " đã bị hủy"
                            + " - Kho: " + contract.getStorageName()
                            + " - Trạng thái cuối: " + contract.getStatus(),
                    contract.getOrder().getCustomer(),
                    contract.getOrder(), null, null, null, null
            );

            eContractRepository.deleteById(id);
        }
    }
}