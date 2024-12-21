package org.ltm.meetingappv2serverjava.service;

import lombok.RequiredArgsConstructor;
import org.ltm.meetingappv2serverjava.DTO.Register;
import org.ltm.meetingappv2serverjava.DTO.UserDTO;
import org.ltm.meetingappv2serverjava.model.Conversation;
import org.ltm.meetingappv2serverjava.model.Message;
import org.ltm.meetingappv2serverjava.model.User;
import org.ltm.meetingappv2serverjava.repository.ConversationRepo;
import org.ltm.meetingappv2serverjava.repository.MessageRepo;
import org.ltm.meetingappv2serverjava.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final MessageRepo messageRepo;
    private final ConversationRepo conversationRepo;

    public User login(String email, String password) {
        User user = userRepo.findByEmailAndPassword(email, password);
        if (user == null) {
            return null;
        }
        user.setOnline(true);
        userRepo.save(user);
        return user;
    }
    public User register(Register register) {
        if (userRepo.findByEmail(register.getEmail()) != null) {
            return null;
        }
        User user = new User(register.getUsername(), register.getEmail(), register.getPassword());
        user.setOnline(true);
        user.setTimestamp(LocalDateTime.now());
        return userRepo.save(user);
    }
    public UserDTO getUserByEmail(String email) {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            return null;
        }
        return new UserDTO(user.getId(),user.getUsername(), user.getEmail(), user.isOnline(), user.getTimestamp().toString(),"");
    }
    public boolean logout(String email) {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            System.out.println("User not found");
            return false;
        }
        user.setOnline(false);
        user.setTimestamp(LocalDateTime.now());
        userRepo.save(user);
        return true;
    }
    public Set<UserDTO> getAllUsers() {
        return userRepo.findAll().stream()
                .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.isOnline(), user.getTimestamp().toString(), ""))
                .collect(Collectors.toSet());
    }
    public List<UserDTO> getAllUsersWithLastMessage(String currentUserId) {
        List<User> allUsers = userRepo.findAll();

        return allUsers.stream().map(user -> {
            // Find the latest conversation involving the user
            List<Conversation> conversations = conversationRepo.findByParticipantsId(user.getId());
            Optional<Message> lastMessage = conversations.stream()
                    .flatMap(conversation -> conversation.getMessages().stream())
                    .filter(message -> message.getSenderId().equals(currentUserId) || message.getReceiverId().equals(currentUserId))
                    .max((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));

            return UserDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .isOnline(user.isOnline())
                    .timeStamp(user.getTimestamp() != null ? user.getTimestamp().toString() : null)
                    .lastMessage(lastMessage.map(Message::getContent).orElse(null)) // Use message content if present
                    .build();
        }).collect(Collectors.toList());
    }

}
