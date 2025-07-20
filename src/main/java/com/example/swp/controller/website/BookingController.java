package com.example.swp.controller.website;

// các import...
import com.example.swp.entity.Order;

import com.example.swp.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class BookingController {

    @Autowired
    private ContractService contractService;

    // giả định phương thức sau khi đặt đơn hàng:
    public String completeBooking(Order savedOrder) {
        contractService.createContractForOrder(savedOrder);
        return "redirect:/contract/success";
    }
}
