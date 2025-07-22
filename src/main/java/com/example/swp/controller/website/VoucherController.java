package com.example.swp.controller.website;

import com.example.swp.annotation.LogActivity;
import com.example.swp.entity.Voucher;
import com.example.swp.entity.VoucherUsage;
import com.example.swp.service.VoucherService;
import com.example.swp.enums.VoucherStatus;
import com.example.swp.service.VoucherUsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/SWP")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private VoucherUsageService voucherUsageService;

    @GetMapping("/staff/vouchers")
    public String showAllVoucherList(Model model) {
        List<Voucher> vouchers = voucherService.getAllVouchers();
        model.addAttribute("vouchers", vouchers);
        return "all-vouchers";
    }

    @GetMapping("/staff/addvoucher")
    public String showAddVoucherForm(Model model) {
        model.addAttribute("voucher", new Voucher());
        return "add-voucher";
    }

    @LogActivity(action = "Tạo voucher mới")
    @PostMapping("/staff/addvoucher")
    public String addVoucher(@ModelAttribute Voucher voucher, RedirectAttributes redirectAttributes) {
        try {
            voucher.setCreatedAt(LocalDateTime.now());
            voucher.setUpdatedAt(LocalDateTime.now());

            if (voucher.getRemainQuantity() == null || voucher.getRemainQuantity() > voucher.getTotalQuantity()) {
                voucher.setRemainQuantity(voucher.getTotalQuantity());
            }

            voucherService.saveVoucher(voucher);
            redirectAttributes.addFlashAttribute("message", "Thêm voucher thành công!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Có lỗi xảy ra khi thêm voucher: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "add-voucher";
        }
        return "redirect:/SWP/staff/vouchers";
    }

    @GetMapping("/staff/vouchers/{id}/edit")
    public String showEditVoucherForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Voucher> voucherOpt = voucherService.getVoucherById(id);
        if (voucherOpt.isPresent()) {
            model.addAttribute("voucher", voucherOpt.get());
            return "staff-edit-voucher";
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy voucher!");
            return "redirect:/SWP/staff/vouchers";
        }
    }

    @LogActivity(action = "Cập nhật voucher")
    @PostMapping("/staff/vouchers/{id}/edit")
    public String editVoucher(@PathVariable Integer id,
                              @ModelAttribute Voucher voucher,
                              RedirectAttributes redirectAttributes) {
        try {
            Optional<Voucher> oldVoucherOpt = voucherService.getVoucherById(id);
            if (oldVoucherOpt.isPresent()) {
                Voucher oldVoucher = oldVoucherOpt.get();
                oldVoucher.setName(voucher.getName());
                oldVoucher.setDescription(voucher.getDescription());
                oldVoucher.setDiscountAmount(voucher.getDiscountAmount());
                oldVoucher.setRequiredPoint(voucher.getRequiredPoint());
                oldVoucher.setStartDate(voucher.getStartDate());
                oldVoucher.setEndDate(voucher.getEndDate());
                oldVoucher.setTotalQuantity(voucher.getTotalQuantity());
                oldVoucher.setRemainQuantity(voucher.getRemainQuantity());
                oldVoucher.setStatus(voucher.getStatus());
                oldVoucher.setUpdatedAt(LocalDateTime.now());
                voucherService.saveVoucher(oldVoucher);
                redirectAttributes.addFlashAttribute("success", "Cập nhật voucher thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy voucher để cập nhật!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật voucher: " + e.getMessage());
        }
        return "redirect:/SWP/staff/vouchers";
    }

    @LogActivity(action = "Xóa voucher")
    @PostMapping("/staff/vouchers/{id}/delete")
    public String deleteVoucher(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            voucherService.deleteVoucher(id);
            redirectAttributes.addFlashAttribute("success", "Xóa voucher thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi xóa voucher: " + e.getMessage());
        }
        return "redirect:/SWP/staff/vouchers";
    }

    @PostMapping("/staff/vouchers/{id}/toggle-status")
    public String toggleVoucherStatus(@PathVariable Integer id,
                                      @RequestParam(required = false) String returnUrl,
                                      RedirectAttributes redirectAttributes) {
        Optional<Voucher> voucherOpt = voucherService.getVoucherById(id);
        if (voucherOpt.isPresent()) {
            Voucher voucher = voucherOpt.get();
            if (voucher.getStatus() == VoucherStatus.ACTIVE) {
                voucher.setStatus(VoucherStatus.INACTIVE);
            } else {
                voucher.setStatus(VoucherStatus.ACTIVE);
            }
            voucher.setUpdatedAt(LocalDateTime.now());
            voucherService.saveVoucher(voucher);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái voucher thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy voucher để cập nhật trạng thái.");
        }

        return "redirect:" + (returnUrl != null ? returnUrl : "/SWP/staff/vouchers");
    }

    @GetMapping("/staff/voucher-usage")
    public String showVoucherUsageHistory(Model model) {
        List<VoucherUsage> usageHistories = voucherUsageService.findAll();
        model.addAttribute("usageHistories", usageHistories);
        return "staff-voucher-usage";
    }

}