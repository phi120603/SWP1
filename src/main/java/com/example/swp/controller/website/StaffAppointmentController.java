package com.example.swp.controller.website;

import com.example.swp.entity.ViewingAppointment;
import com.example.swp.service.EmailService;
import com.example.swp.service.ViewingAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class StaffAppointmentController {

    private final ViewingAppointmentService viewingAppointmentService;
    private final EmailService emailService;

    @GetMapping("/staff/appointments")
    public String listAppointments(Model model,
                                   @RequestParam Optional<String> status,
                                   @RequestParam Optional<String> warehouse,
                                   @RequestParam Optional<String> fromDate,
                                   @RequestParam Optional<String> toDate,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "5") int size) {

        Page<ViewingAppointment> appointmentsPage = viewingAppointmentService
                .findFilteredPaginated(status.orElse(null), warehouse.orElse(null),
                        fromDate.orElse(null), toDate.orElse(null),
                        page, size);

        model.addAttribute("appointmentsPage", appointmentsPage);
        model.addAttribute("currentStatus", status.orElse(""));
        model.addAttribute("currentWarehouse", warehouse.orElse(""));
        model.addAttribute("currentFromDate", fromDate.orElse(""));
        model.addAttribute("currentToDate", toDate.orElse(""));
        return "staff-appointment-list";
    }


    // Xem chi tiết 1 lịch hẹn (chưa cần xử lý logic nhiều, để mẫu trước)
    @GetMapping("/staff/appointments/{id}")
    public String viewAppointmentDetail(@PathVariable int id, Model model) {
        Optional<ViewingAppointment> optionalAppointment = viewingAppointmentService.findById(id);
        if (optionalAppointment.isEmpty()) {
            return "redirect:/staff/appointments?error=AppointmentNotFound";
        }

        model.addAttribute("appointment", optionalAppointment.get());
        return "appointment-detail";
    }

    @PostMapping("/staff/appointments/{id}/handle")
    public String handleAppointmentAction(@PathVariable int id,
                                          @RequestParam String action,
                                          @RequestParam(required = false) String reason,
                                          RedirectAttributes redirectAttributes) {

        Optional<ViewingAppointment> optional = viewingAppointmentService.findById(id);
        if (optional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Lịch hẹn không tồn tại");
            return "redirect:/staff/appointments";
        }

        ViewingAppointment appointment = optional.get();

        if (action.equals("ACCEPTED")) {
            appointment.setStatus("ACCEPTED");
            viewingAppointmentService.save(appointment);
            emailService.sendEmail(appointment.getEmail(), "Lịch hẹn được chấp nhận", "Lịch hẹn của bạn đã được chấp nhận.");
        } else if (action.equals("REJECTED")) {
            appointment.setStatus("REJECTED");
            appointment.setRejectReason(reason);
            viewingAppointmentService.save(appointment);
            emailService.sendEmail(
                    appointment.getEmail(),
                    "Lịch hẹn bị từ chối",
                    "Lịch hẹn của bạn đã bị từ chối. Lý do: " + reason
            );
        }

        return "redirect:/staff/appointments";
    }

}
