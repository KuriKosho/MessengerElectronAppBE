package org.ltm.meetingappv2serverjava.service;

import lombok.RequiredArgsConstructor;
import org.ltm.meetingappv2serverjava.model.Conversation;
import org.ltm.meetingappv2serverjava.model.Message;
import org.ltm.meetingappv2serverjava.model.User;
import org.ltm.meetingappv2serverjava.repository.ConversationRepo;
import org.ltm.meetingappv2serverjava.repository.MessageRepo;
import org.ltm.meetingappv2serverjava.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepo messageRepo;
    private final ConversationRepo conversationRepo;
    private final UserRepo userRepo;

    public List<Message> getMessages(String senderId, String userToChatId) {
        try {
            List<String> participants = List.of(senderId, userToChatId);
            return conversationRepo.findByParticipantsContainingAll(participants)
                    .map(Conversation::getMessages)
                    .orElse(Collections.emptyList());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving messages: " + e.getMessage(), e);
        }
    }
    public Message saveMessage(Message message) {
        try {
            Optional<Conversation> conversation = conversationRepo.findByParticipantsContainingAll(List.of(message.getSenderId(), message.getReceiverId()));
            if (conversation.isEmpty()) {
                User sender = userRepo.findById(message.getSenderId()).orElseThrow(() -> new RuntimeException("Sender not found"));
                User receiver = userRepo.findById(message.getReceiverId()).orElseThrow(() -> new RuntimeException("Receiver not found"));
                conversation = Optional.of(Conversation.builder()
                        .participants(List.of(sender, receiver))
                        .messages(List.of(message))
                        .build());

            } else {
                conversation.get().getMessages().add(message);
            }
            messageRepo.save(message);
            conversationRepo.save(conversation.get());
            return message;
        } catch (Exception e) {
            throw new RuntimeException("Error saving message: " + e.getMessage(), e);

        }
    }
    public List<Message> findChatMessages(String senderId, String receiverId) {
        try {
            List<Message> messages = conversationRepo.findByParticipantsContainingAll(List.of(senderId, receiverId))
                    .map(Conversation::getMessages)
                    .orElse(Collections.emptyList());
            return messages;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving messages: " + e.getMessage(), e);
        }
    }
}