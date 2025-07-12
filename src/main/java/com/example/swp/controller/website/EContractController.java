package com.example.swp.controller.website;

import com.example.swp.entity.EContract;
import com.example.swp.service.EContractService;
import com.example.swp.service.NotificationService;
import com.example.swp.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/econtract")
public class EContractController {

    private final EContractService eContractService;
    private final NotificationService notificationService;
    private final CustomerService customerService;

    @PostMapping("/create")
    public String createEContract(@ModelAttribute EContract contract) {
        EContract saved = eContractService.createContract(contract);
        notificationService.notifyCustomer(
                saved.getCustomer().getId(),
                "Hợp đồng mới đã được tạo. Vui lòng đăng nhập để xem và ký hợp đồng."
        );
        return "redirect:/econtract/customer";
    }

    @GetMapping("/customer")
    public String customerContracts(Model model) {
        Integer fakeCustomerId = 1; // Replace with real one via auth/session
        model.addAttribute("contracts", eContractService.getContractsByCustomerId(fakeCustomerId));
        return "contract/customer-list";
    }

    @PostMapping("/sign/{id}")
    public String signContract(@PathVariable Integer id) {
        eContractService.signContract(id);
        return "redirect:/payment?contractId=" + id;
    }
}
