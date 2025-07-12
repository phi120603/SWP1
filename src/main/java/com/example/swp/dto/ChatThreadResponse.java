package com.example.swp.dto;

import java.time.LocalDateTime;

public class ChatThreadResponse {
    private String roomId;
    private String userId;       // ID của người dùng gửi đến Manager
    private String username;     // Tên người dùng (nếu có)
    private String lastMessage;
    private LocalDateTime timestamp;

    public ChatThreadResponse(String roomId, String userId, String username, String lastMessage, LocalDateTime timestamp) {
        this.roomId = roomId;
        this.userId = userId;
        this.username = username;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    // Getters & Setters
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
