package com.projektsse.backend.controller.ws;

import java.time.Instant;
import java.util.UUID;

public record ChatMessage(
        UUID senderId,
        UUID recipientId,
        String senderName,
        String content,
        MessageType type,
        Instant send_at
) {

    public enum MessageType {CHAT, JOIN, LEAVE}

    public ChatMessage setTime() {
        return new ChatMessage(senderId, recipientId, senderName, content, type, Instant.now());
    }
}
