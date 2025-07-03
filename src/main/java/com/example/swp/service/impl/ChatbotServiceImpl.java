package com.example.swp.service.impl;

import com.example.swp.entity.Customer;
import com.example.swp.entity.Storage;
import com.example.swp.entity.Order;
import com.example.swp.repository.CustomerRepository;
import com.example.swp.repository.StorageReponsitory;
import com.example.swp.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private StorageReponsitory storageRepository;

    @Autowired
    private OrderRepository orderRepository;

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

        // 4. Ghép thành prompt cho AI
        String prompt = customerInfo
                + "\n" + storageInfo
                + "\n" + orderInfo
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
