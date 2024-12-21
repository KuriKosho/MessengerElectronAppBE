package org.ltm.meetingappv2serverjava.config;

import org.ltm.meetingappv2serverjava.repository.UserRepo;
import org.ltm.meetingappv2serverjava.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.util.HashSet;
import java.util.Set;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    @Autowired
    private UserService userService;
    private Set<String> onlineUsers = new HashSet<>();  // Set to track online users
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        System.out.println("headerAccessor: " + headerAccessor);
        System.out.println("username :"+ convertHeaderAccessorToUsername(headerAccessor));
        String username = convertHeaderAccessorToUsername(headerAccessor);
        if (username != null) {
//            messagingTemplate.convertAndSendToUser(username, "/queue/notifications", "You are connected");
//            logger.info("Sent notification to user: " + username);
            onlineUsers.add(username);  // Add username to the online user list
            messagingTemplate.convertAndSend("/topic/users", userService.getAllUsers());  // Send online users list to all clients
//            userService.getAllUsers();

        }
        logger.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if(username != null) {
            onlineUsers.remove(username);  // Remove username from the online user list
            messagingTemplate.convertAndSend("/topic/users", userService.getAllUsers());  // Update the user list for all clients
            logger.info("User disconnected: " + username);
        }
        System.out.println("Received a new web socket disconnection");
    }
    public String convertHeaderAccessorToUsername(StompHeaderAccessor headerAccessor) {
//        Delete [] from the return statement
        String username = headerAccessor.toString().split("user_id=")[1].split(",")[0];
        username = username.substring(1, username.length() - 1);
        return username;
    }
}
