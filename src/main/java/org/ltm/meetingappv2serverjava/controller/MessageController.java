package org.ltm.meetingappv2serverjava.controller;

import org.ltm.meetingappv2serverjava.DTO.FileUpload;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
    @PostMapping("/api/upload")
    public Response<String> handleFileUpload(@RequestParam String senderId,
                                             @RequestParam String receiverId,
                                             @RequestParam("file") MultipartFile file) {
        try {
            System.out.println("File: " + file.getOriginalFilename());
            System.out.println("Sender: " + senderId);
            System.out.println("Receiver: " + receiverId);
            String filePath = messageService.uploadFile(file, senderId, receiverId);
            if (filePath == null) {
                return Response.<String>builder()
                        .success(false)
                        .data(null)
                        .build();
            }
            Message newMessage = Message.builder()
                    .senderId(senderId)
                    .receiverId(receiverId)
                    .content(filePath)
                    .timestamp(LocalDateTime.now())
                    .build();
            // Gửi thông báo tới người nhận
            messagingTemplate.convertAndSendToUser(
                    receiverId, "/queue/messages",
                    newMessage
            );

            return Response.<String>builder()
                    .success(true)
                    .data(filePath)
                    .build();
        } catch (Exception e) {
            return Response.<String>builder()
                    .success(false)
                    .data(null)
                    .build();
        }
    }

}
