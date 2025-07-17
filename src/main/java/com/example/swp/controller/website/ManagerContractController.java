package com.example.swp.controller.website;

import com.example.swp.entity.EContract;
import com.example.swp.enums.EContractStatus;
import com.example.swp.repository.EContractRepository;
import com.example.swp.service.ContractService;
import com.example.swp.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/manager/contracts")
public class ManagerContractController {

    @Autowired
    private ContractService contractService;

    @GetMapping
    public String showAllContracts(Model model) {
        List<EContract> contracts = contractService.getAllContracts();
        model.addAttribute("contracts", contracts);
        model.addAttribute("statuses", EContractStatus.values()); // Dùng cho dropdown
        return "manager/contract-management";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        EContract contract = contractService.getContractById(id).orElse(null);
        if (contract == null) return "redirect:/manager/contracts";
        model.addAttribute("contract", contract);
        model.addAttribute("statuses", EContractStatus.values()); // thêm dòng này để dropdown hoạt động
        return "manager/edit-contract";
    }

    @PostMapping("/edit")
    public String updateContract(@ModelAttribute EContract contract) {
        contractService.updateContract(contract);
        return "redirect:/manager/contracts";
    }

    @PostMapping("/delete/{id}")
    public String deleteContract(@PathVariable Long id) {
        contractService.deleteContract(id);
        return "redirect:/manager/contracts";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam Long id, @RequestParam String status) {
        contractService.updateContractStatus(id, status);
        return "redirect:/manager/contracts";
    }
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private EContractRepository eContractRepository;

    @PostMapping("/update-status/{id}")
    public String updateContractStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        Optional<EContract> optionalContract = eContractRepository.findById(id);
        if (optionalContract.isPresent()) {
            EContract contract = optionalContract.get();
            contract.setStatus(EContractStatus.valueOf(status));
            eContractRepository.save(contract);

            // Gửi thông báo cho Customer của hợp đồng
            String message = "Trạng thái hợp đồng " + contract.getContractCode() + " đã được cập nhật thành: " + status;
            notificationService.createNotification(message, contract.getOrder().getCustomer());
        }
        return "redirect:/manager/contracts";
    }

}
