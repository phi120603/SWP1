package com.example.swp.controller.website;

import com.example.swp.entity.EContract;
import com.example.swp.enums.EContractStatus;
import com.example.swp.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/contracts")
public class ContractManagerController {

    @Autowired
    private ContractService contractService;

    @GetMapping
    public String showAllContracts(Model model) {
        List<EContract> contracts = contractService.getAllContracts();
        model.addAttribute("contracts", contracts);
        model.addAttribute("statuses", EContractStatus.values());  // Thêm dòng này để hiển thị dropdown
        return "manager-contract-list";
    }

    @PostMapping("/change-status/{id}")
    public String changeContractStatus(@PathVariable Long id,
                                       @RequestParam("status") String status) {
        contractService.updateStatus(id, EContractStatus.valueOf(status));
        return "redirect:/admin/contracts";
    }
}
