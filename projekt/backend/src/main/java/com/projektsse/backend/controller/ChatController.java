package com.projektsse.backend.controller;

import com.projektsse.backend.controller.ws.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(ChatMessage chatMessage) {
        return chatMessage.setTime();
    }

    @MessageMapping("/chat/private")
    public void sendPrivateMessage(ChatMessage chatMessage) {
        ChatMessage msg = chatMessage.setTime();
        messagingTemplate.convertAndSendToUser(
                chatMessage.recipientId().toString(),
                "/queue/messages",
                msg
        );
    }

    @MessageExceptionHandler
    @SendTo("/topic/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }
}
