package com.example.swp.controller.website;

import com.example.swp.entity.Customer;
import com.example.swp.entity.EContract;
import com.example.swp.enums.EContractStatus;
import com.example.swp.service.ContractService;
import com.example.swp.service.EmailService;
import com.example.swp.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.OutputStream;
import java.util.Optional;

@Controller
@RequestMapping("/econtract")
public class ContractController {

    @Autowired
    private ContractService contractService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * Hiển thị trang ký hợp đồng:
     * - Nếu chưa ký → trả về view "view-contract" (form ký).
     * - Nếu đã ký  → trả về view "contract-sign-success".
     */
    @GetMapping("/view/{id}")
    public String viewContract(@PathVariable("id") Long id,
                               HttpSession session,
                               Model model) {
        // Kiểm tra đang login
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return "redirect:/api/login";
        }

        // Lấy hợp đồng
        Optional<EContract> opt = contractService.getContractById(id);
        if (opt.isEmpty()) {
            // Không tìm thấy → quay về danh sách đơn
            return "redirect:/SWP/customers/my-bookings";
        }
        EContract contract = opt.get();

        // Kiểm tra quyền sở hữu hợp đồng
        if (contract.getOrder().getCustomer().getId() != customer.getId()) {
            return "redirect:/SWP/customers/my-bookings";
        }

        // Đưa contract và order vào model
        model.addAttribute("contract", contract);
        model.addAttribute("order",    contract.getOrder());

        // Nếu đã ký rồi → chuyển sang trang thành công
        if (contract.getStatus() == EContractStatus.SIGNED) {
            return "view-contract";
        }

        // Chưa ký → hiển thị form ký
        return "view-contract";
    }

    /**
     * Xử lý logic khi user submit form ký:
     * - Đánh dấu đã ký
     * - Tạo link thanh toán VNPay
     * - Gửi email xác nhận
     * - Trả về trang success
     */
    @PostMapping("/sign/{id}")
    public String signContract(@PathVariable("id") Long id,
                               HttpSession session,
                               Model model,
                               HttpServletRequest request) throws Exception {
        // Kiểm tra đang login
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return "redirect:/api/login";
        }

        // Lấy hợp đồng
        Optional<EContract> opt = contractService.getContractById(id);
        if (opt.isEmpty()) {
            return "redirect:/api/login";
        }
        EContract contract = opt.get();

        // Nếu chưa ký & đúng chủ → thực hiện ký
        if (contract.getStatus() != EContractStatus.SIGNED
                && contract.getOrder().getCustomer().getId() == customer.getId()) {

            // 1) đánh dấu đã ký
            contractService.signContract(contract);

            // 2) tạo link thanh toán VNPay
            long amount  = contract.getTotalAmount().longValue();
            int  orderId = contract.getOrder().getId();
            String paymentLink = vnPayService.createVNPayUrl(request, amount, orderId);

            // 3) gửi email xác nhận
            String subject = "✔️ Hợp đồng " + contract.getContractCode() + " đã ký thành công";
            String body = String.format(
                    "Xin chào %s,\n\n" +
                            "Bạn đã ký hợp đồng mã %s thành công.\n" +
                            "Vui lòng hoàn thành thanh toán tại đường dẫn sau:\n%s\n\n" +
                            "Cảm ơn bạn,\n" +
                            "Đội ngũ Quản lý kho",
                    customer.getFullname(),
                    contract.getContractCode(),
                    paymentLink
            );
            emailService.sendEmail(customer.getEmail(), subject, body);

            // Trả về trang success
            model.addAttribute("contract",    contract);
            model.addAttribute("paymentLink", paymentLink);
            return "contract-sign-success";
        }

        // Nếu đã ký rồi hoặc không hợp lệ → quay về trang xem
        return "redirect:/econtract/view/" + id;
    }

    /**
     * Xuất hợp đồng ra PDF:
     * - Build HTML từ view-contract
     * - Render PDF bằng Flying Saucer (ITextRenderer)
     */
    @GetMapping("/export-pdf/{id}")
    public void exportContractToPdf(@PathVariable("id") Long id,
                                    HttpServletResponse response) throws Exception {
        Optional<EContract> optionalContract = contractService.getContractById(id);
        if (optionalContract.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Hợp đồng không tồn tại");
            return;
        }
        EContract contract = optionalContract.get();

        // Chuẩn bị dữ liệu cho Thymeleaf
        Context ctx = new Context();
        ctx.setVariable("contract", contract);
        ctx.setVariable("order",    contract.getOrder());
        String html = templateEngine.process("view-contract", ctx);

        // Thiết lập header và content-type
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=contract-" + contract.getContractCode() + ".pdf");

        // Render PDF
        try (OutputStream out = response.getOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.getFontResolver()
                    .addFont("src/main/resources/fonts/Roboto-Regular.ttf",
                            com.lowagie.text.pdf.BaseFont.IDENTITY_H,
                            com.lowagie.text.pdf.BaseFont.EMBEDDED);
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(out);
        }
    }
}