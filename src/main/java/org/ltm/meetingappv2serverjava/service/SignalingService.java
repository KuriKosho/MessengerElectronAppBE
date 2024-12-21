package org.ltm.meetingappv2serverjava.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ltm.meetingappv2serverjava.DTO.SignalMessage;
import org.ltm.meetingappv2serverjava.model.Conversation;
import org.ltm.meetingappv2serverjava.model.Message;
import org.ltm.meetingappv2serverjava.model.User;
import org.ltm.meetingappv2serverjava.repository.ConversationRepo;
import org.ltm.meetingappv2serverjava.repository.MessageRepo;
import org.ltm.meetingappv2serverjava.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class SignalingService {

    private final ConversationRepo conversationRepository;
    private final MessageRepo messageRepository;
    private final UserRepo userRepository;

    // Quản lý trạng thái kết nối giữa các người dùng
    private final ConcurrentHashMap<String, String> userConnections = new ConcurrentHashMap<>();

    public SignalMessage handleJoin(SignalMessage message) {
        log.info("User joined: {}", message.getSenderId());
        userConnections.put(message.getSenderId(), message.getReceiverId());

        // Update user status to online
        Optional<User> userOptional = userRepository.findById(message.getSenderId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setOnline(true);
            user.setTimestamp(LocalDateTime.now());
            userRepository.save(user);
        }
        return message;
    }

    public SignalMessage handleOffer(SignalMessage message) {
        log.info("Received offer from: {} to: {}", message.getSenderId(), message.getReceiverId());

        // Create a new conversation if not exists
        Conversation conversation = getOrCreateConversation(message.getSenderId(), message.getReceiverId());

        // Log the offer as a message
        Message offerMessage = Message.builder()
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .content("Offer SDP")
                .timestamp(LocalDateTime.now())
                .build();
        messageRepository.save(offerMessage);

        conversation.getMessages().add(offerMessage);
        conversation.setTimestamp(LocalDateTime.now());
        conversationRepository.save(conversation);

        return message;
    }

    public SignalMessage handleAnswer(SignalMessage message) {
        log.info("Received answer from: {} to: {}", message.getSenderId(), message.getReceiverId());

        // Update the conversation with the answer message
        Conversation conversation = getOrCreateConversation(message.getSenderId(), message.getReceiverId());

        Message answerMessage = Message.builder()
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .content("Answer SDP")
                .timestamp(LocalDateTime.now())
                .build();
        messageRepository.save(answerMessage);

        conversation.getMessages().add(answerMessage);
        conversation.setTimestamp(LocalDateTime.now());
        conversationRepository.save(conversation);

        return message;
    }

    public SignalMessage handleCandidate(SignalMessage message) {
        log.info("Received ICE Candidate from: {} to: {}", message.getSenderId(), message.getReceiverId());

        // Log the candidate as a message
        Message candidateMessage = Message.builder()
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .content("ICE Candidate")
                .timestamp(LocalDateTime.now())
                .build();
        messageRepository.save(candidateMessage);

        return message;
    }

    public SignalMessage handleLeave(SignalMessage message) {
        log.info("User left: {}", message.getSenderId());
        userConnections.remove(message.getSenderId());

        // Update user status to offline
        Optional<User> userOptional = userRepository.findById(message.getSenderId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setOnline(false);
            user.setTimestamp(LocalDateTime.now());
            userRepository.save(user);
        }
        return message;
    }

    public SignalMessage handleStop(SignalMessage message) {
        log.info("Call stopped by: {}", message.getSenderId());

        // Log the stop action as a message
        Message stopMessage = Message.builder()
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .content("Call Stopped")
                .timestamp(LocalDateTime.now())
                .build();
        messageRepository.save(stopMessage);

        return message;
    }

    private Conversation getOrCreateConversation(String senderId, String receiverId) {
        // Check if conversation exists
        Optional<Conversation> conversationOptional = conversationRepository.findByParticipants(senderId, receiverId);
        if (conversationOptional.isPresent()) {
            return conversationOptional.get();
        }

        // Create a new conversation
        Conversation newConversation = Conversation.builder()
                .participants(List.of(userRepository.findById(senderId).orElseThrow(),
                        userRepository.findById(receiverId).orElseThrow()))
                .messages(new ArrayList<>())
                .timestamp(LocalDateTime.now())
                .build();

        return conversationRepository.save(newConversation);
    }
}
