package com.projektsse.backend.controller.ws;

public record ChatMessage(String sender, String content, MessageType type) {
    public enum MessageType { CHAT, JOIN, LEAVE }
}
