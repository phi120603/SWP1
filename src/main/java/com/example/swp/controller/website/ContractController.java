package com.example.swp.controller.website;

import com.example.swp.entity.Customer;
import com.example.swp.entity.EContract;
import com.example.swp.enums.EContractStatus;
import com.example.swp.service.ContractService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.OutputStream;
import java.util.Optional;

@Controller
@RequestMapping("/econtract")
public class ContractController {

    @Autowired
    private ContractService contractService;

    @GetMapping("/view/{id}")
    public String viewContract(@PathVariable Long id, HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");

        if (customer == null) {
            return "redirect:/login";  // Hoặc chuyển hướng phù hợp
        }

        EContract contract = contractService.getContractById(id).orElse(null);

        if (contract == null || contract.getOrder() == null || contract.getOrder().getCustomer() == null
                || contract.getOrder().getCustomer().getId() != customer.getId()) {
            return "redirect:/SWP/customers/my-bookings";
        }


        model.addAttribute("contract", contract);
        model.addAttribute("order", contract.getOrder());
        return "view-contract";
    }

    @PostMapping("/sign/{id}")
    public String signContract(@PathVariable Long id, HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        EContract contract = contractService.getContractById(id).orElse(null);

        if (contract != null
                && contract.getOrder().getCustomer().getId() == customer.getId()
                && contract.getStatus() != EContractStatus.SIGNED) {

            contractService.signContract(contract);
            model.addAttribute("contract", contract);
            return "contract-sign-success";
        }
        return "redirect:/econtract/view/" + id;


        // Nếu đã ký rồi hoặc không hợp lệ → chuyển về view hợp đồng

    }
    @Autowired
    private TemplateEngine templateEngine;

    @GetMapping("/export-pdf/{id}")
    public void exportContractToPdf(@PathVariable Long id, HttpServletResponse response) throws Exception {
        Optional<EContract> optionalContract = contractService.getContractById(id);
        if (optionalContract.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Hợp đồng không tồn tại");
            return;
        }

        EContract contract = optionalContract.get();

        // Tạo context Thymeleaf
        Context context = new Context();
        context.setVariable("contract", contract);
        context.setVariable("order", contract.getOrder());

        // Render HTML bằng Thymeleaf
        String html = templateEngine.process("view-contract", context);

        // Gửi về browser như PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=contract-" + contract.getContractCode() + ".pdf");

        OutputStream out = response.getOutputStream();

        ITextRenderer renderer = new ITextRenderer();

        // ✅ Thêm dòng dưới để hỗ trợ tiếng Việt:
        String fontPath = "src/main/resources/fonts/Roboto-Regular.ttf";
        renderer.getFontResolver().addFont(fontPath, com.lowagie.text.pdf.BaseFont.IDENTITY_H, com.lowagie.text.pdf.BaseFont.EMBEDDED);

        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(out);
        out.close();
    }

}