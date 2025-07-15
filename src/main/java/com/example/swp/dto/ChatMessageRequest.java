package com.example.swp.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {
    private String roomId;     // mỗi cuộc chat giữa manager và 1 customer
    private String senderId;   // người gửi
    private String content;    // nội dung tin nhắn
}
