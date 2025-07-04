package com.example.swp.service.impl;

import com.example.swp.entity.*;
import com.example.swp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.swp.service.ChatbotService;

import java.util.*;

@Service
public class ChatbotServiceImpl implements ChatbotService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StaffRepository staffReponsitory;

    private static final String API_KEY = "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";
    private static final String OPENROUTER_API_URL = "https://openrouter.ai/api/v1/chat/completions";

    @Override
    public String askAI(String message) {
        RestTemplate restTemplate = new RestTemplate();

        // 1. Lấy dữ liệu khách hàng
        List<Customer> customers = customerRepository.findAll();
        StringBuilder customerInfo = new StringBuilder("Dưới đây là một số khách hàng:\n");
        for (Customer c : customers) {
            customerInfo.append("- Tên: ").append(c.getFullname())
                    .append(", Địa chỉ: ").append(c.getAddress())
                    .append(", SĐT: ").append(c.getPhone())
                    .append(", Email: ").append(c.getEmail())
                    .append("\n");
        }

        // 2. Lấy dữ liệu kho bãi
        List<Storage> storages = storageRepository.findAll();
        StringBuilder storageInfo = new StringBuilder("Danh sách kho bãi:\n");
        for (Storage s : storages) {
            storageInfo.append("- Tên kho: ").append(s.getStoragename())
                    .append(", Địa chỉ: ").append(s.getAddress())
                    .append("\n");
        }

        // 3. Lấy dữ liệu đơn hàng (chỉ lấy một số đơn gần nhất, tránh quá dài)
        List<Order> orders = orderRepository.findAll();
        StringBuilder orderInfo = new StringBuilder("Một số đơn hàng gần đây:\n");
        int orderCount = 0;
        for (Order o : orders) {
            if (orderCount++ >= 5) break; // Chỉ lấy 5 đơn gần nhất (nếu muốn)
            orderInfo.append("- Mã đơn: ").append(o.getId())
                    .append(", Khách: ").append(o.getCustomer().getFullname())
                    .append(", Ngày: ").append(o.getOrderDate())
                    .append("\n");
        }

        // Lấy feedback gần đây nhất
        List<Feedback> feedbacks = feedbackRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        StringBuilder feedbackInfo = new StringBuilder("Một số phản hồi gần đây:\n");
        int count = 0;
        for (Feedback f : feedbacks) {
            if (++count > 3) break;
            feedbackInfo.append("- Khách: ").append(f.getCustomer().getFullname())
                    .append(", Kho: ").append(f.getStorage().getStoragename())
                    .append(", Đánh giá: ").append(f.getRating())
                    .append(", Nội dung: ").append(f.getContent()).append("\n");
        }


        // Lấy 3 đơn xin nghỉ phép gần nhất
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findAll(
                Sort.by(Sort.Direction.DESC, "startDate")
        );
        StringBuilder leaveRequestInfo = new StringBuilder("Đơn xin nghỉ phép gần đây:\n");
        int countLeaveRequest = 0;
        for (LeaveRequest lr : leaveRequests) {
            if (++countLeaveRequest > 3) break;
            leaveRequestInfo.append("- Nhân viên: ").append(lr.getStaff().getFullname())
                    .append(", Lý do: ").append(lr.getReason())
                    .append(", Từ: ").append(lr.getFromDate())
                    .append(" đến: ").append(lr.getToDate())
                    .append(", Trạng thái: ").append(lr.getStatus())
                    .append("\n");
        }

        // Lấy 3 lần chấm công gần nhất
        List<Attendance> attendances = attendanceRepository.findAll(
                Sort.by(Sort.Direction.DESC, "checkInTime")
        );

        StringBuilder attendanceInfo = new StringBuilder("Chấm công gần đây:\n");
        int countAttendance = 0;
        for (Attendance a : attendances) {
            if (++countAttendance > 3) break;
            attendanceInfo.append("- Nhân viên: ").append(a.getStaff().getFullname())
                    .append(", Vào: ").append(a.getCheckInTime())
                    .append(", Ra: ").append(a.getCheckOutTime())
                    .append("\n");
        }

        // Lấy danh sách nhân viên
        List<Staff> staffList = staffReponsitory.findAll();
        StringBuilder staffInfo = new StringBuilder("Danh sách nhân viên:\n");
        for (Staff s : staffList) {
            staffInfo.append("- Tên: ").append(s.getFullname())
                    .append(", Email: ").append(s.getEmail())
                    .append(", SĐT: ").append(s.getPhone())
                    .append("\n");
        }

        // 4. Ghép thành prompt cho AI
        String prompt = customerInfo
                + "\n" + storageInfo
                + "\n" + orderInfo
                + "\n" + feedbackInfo
                + "\n" + leaveRequestInfo
                + "\n" + attendanceInfo
                + "\n" + staffInfo
                + "\nDựa vào các thông tin trên, hãy hỗ trợ/trả lời khách hàng với câu hỏi sau:\n"
                + message;

        // 5. Build messages list
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> msg = new HashMap<>();
        msg.put("role", "user");
        msg.put("content", prompt);
        messages.add(msg);

        // 6. Build body giữ nguyên
        Map<String, Object> body = new HashMap<>();
        body.put("model", "openai/gpt-4o");
        body.put("messages", messages);
        body.put("max_tokens", 256);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);
        headers.set("HTTP-Referer", "https://openrouter.ai");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(OPENROUTER_API_URL, entity, Map.class);
            Map<String, Object> result = response.getBody();
            List<Map<String, Object>> choices = (List<Map<String, Object>>) result.get("choices");
            Map<String, Object> messageMap = (Map<String, Object>) choices.get(0).get("message");
            return messageMap.get("content").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Xin lỗi, AI đang gặp sự cố!";
        }
    }
}
