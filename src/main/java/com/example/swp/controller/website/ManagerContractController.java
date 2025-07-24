package com.example.swp.controller.website;

import com.example.swp.entity.EContract;
import com.example.swp.enums.EContractStatus;
import com.example.swp.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/contracts")
public class ManagerContractController {

    @Autowired
    private ContractService contractService;

    @GetMapping
    public String viewContracts(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "7") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EContract> contractPage = contractService.getContractsPage(pageable); // ✅ gọi đúng hàm

        model.addAttribute("contracts", contractPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contractPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("statuses", EContractStatus.values());

        return "manager-contract-list";
    }

    @PostMapping("/change-status/{id}")
    public String changeContractStatus(@PathVariable Integer id, @RequestParam("status") String status) {
        contractService.updateContractStatus(id.longValue(), status);
        return "redirect:/admin/contracts";
    }
}
