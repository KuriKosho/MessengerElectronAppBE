package org.ltm.meetingappv2serverjava.controller;

import org.ltm.meetingappv2serverjava.DTO.MessageDTO;
import org.ltm.meetingappv2serverjava.DTO.MessageRequest;
import org.ltm.meetingappv2serverjava.DTO.Response;
import org.ltm.meetingappv2serverjava.model.Message;
import org.ltm.meetingappv2serverjava.repository.ConversationRepo;
import org.ltm.meetingappv2serverjava.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RestController
public class MessageController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void processMessage(@Payload MessageDTO message) {
        System.out.println("Received message: " + message.toString());
        Message newMessage = Message.builder()
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .content(message.getContent())
                .timestamp(LocalDateTime.now())
                .build();
        messageService.saveMessage(newMessage);
        messagingTemplate.convertAndSendToUser(
                newMessage.getReceiverId(), "/queue/messages",
                newMessage
        );
    }

    @GetMapping("/api/messages")
        public Response<List<Message>> findChatMessages(@RequestParam String senderId,
                                                    @RequestParam String receiverId) {
        List<Message> messages = messageService.findChatMessages(senderId, receiverId);
        return new Response<>(true, messages);

    }

}
