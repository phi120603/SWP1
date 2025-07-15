package com.example.swp.controller.website;

import com.example.swp.dto.EContractRequest;
import com.example.swp.entity.Customer;
import com.example.swp.entity.EContract;
import com.example.swp.service.CustomerService;
import com.example.swp.service.EContractService;
import com.example.swp.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/econtract")
public class EContractController {

    private final EContractService eContractService;
    private final NotificationService notificationService;
    private final CustomerService customerService;

    // Tạo hợp đồng từ phía backend (sau khi đặt kho)
    @PostMapping("/create")
    public String createEContract(@ModelAttribute EContract contract) {
        EContract saved = eContractService.createContract(contract);
        notificationService.notifyCustomer(
                saved.getCustomer().getId(),
                "Hợp đồng mới đã được tạo. Vui lòng đăng nhập để xem và ký hợp đồng."
        );
        return "redirect:/econtract/customer";
    }

    // Danh sách hợp đồng của khách hàng
    @GetMapping("/customer")
    public String customerContracts(Model model, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return "redirect:/login";
        }

        List<EContract> contracts = eContractService.getContractsByCustomerId(customer.getId());
        model.addAttribute("contracts", contracts);
        return "contract/customer-list";
    }

    // Xem trước hợp đồng
    @GetMapping("/preview/{id}")
    public String previewContract(@PathVariable Integer id, Model model, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) return "redirect:/login";

        EContract contract = eContractService.findById(id);
        if (contract == null || contract.getCustomer().getId() != customer.getId()) {
            return "redirect:/error";
        }



        model.addAttribute("contract", contract);
        return "econtract/preview";
    }

    // Ký hợp đồng
    @PostMapping("/sign/{id}")
    public String signContract(@PathVariable Integer id, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) return "redirect:/login";

        EContract contract = eContractService.findById(id);
        if (contract == null || contract.getCustomer().getId() != customer.getId()) {
            return "redirect:/error";
        }

        eContractService.signContract(id);

        notificationService.notifyCustomer(customer.getId(),
                "Bạn đã ký hợp đồng #" + id + ". Vui lòng thanh toán trong vòng 3 ngày.");

        return "redirect:/econtract/confirm-payment?contractId=" + id;
    }

    // Trang xác nhận thanh toán sau khi ký
    @GetMapping("/confirm-payment")
    public String confirmPaymentPage(@RequestParam("contractId") Integer contractId, Model model, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return "redirect:/login";
        }

        model.addAttribute("customerName", customer.getFullname());
        model.addAttribute("contractId", contractId);
        return "contract/confirm-payment";
    }
    @GetMapping("/econtract/view/{id}")
    public String viewContract(@PathVariable int  id, Model model) {
        EContract econtract = eContractService.findById(id);
        if (econtract != null) {
            EContractRequest request = new EContractRequest(
                    econtract.getId(), econtract.getContractUrl()
            );
            model.addAttribute("contract", request);
        }
        return "econtract/contract-view"; // trỏ đến file HTML bạn vừa tạo
    }

}
