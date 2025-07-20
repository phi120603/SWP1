package com.example.swp.controller.website;

import com.example.swp.entity.Customer;
import com.example.swp.entity.EContract;
import com.example.swp.enums.EContractStatus;
import com.example.swp.service.ContractService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class ContractController {

    @Autowired
    private ContractService contractService;

    @GetMapping("/contract/{id}")
    public String viewContract(@PathVariable("id") Integer id,
                               HttpSession session,
                               Model model) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return "redirect:/api/login";
        }

        Optional<EContract> opt = contractService.getContractById(id);
        if (opt.isEmpty()) {
            return "redirect:/SWP/customers/my-bookings";
        }

        EContract contract = opt.get();

        if (contract.getOrder().getCustomer().getId() != customer.getId()) {
            return "redirect:/SWP/customers/my-bookings";
        }

        model.addAttribute("contract", contract);
        model.addAttribute("order", contract.getOrder());

        return "view-contract";
    }
}
