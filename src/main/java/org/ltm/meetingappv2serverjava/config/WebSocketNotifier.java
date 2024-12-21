//package org.ltm.meetingappv2serverjava.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Component;
//
//import java.util.Set;
//
////
////@Component
////public class WebSocketNotifier {
////
////    @Autowired
////    private SimpMessagingTemplate messagingTemplate;
////
////    @Autowired
////    private WebSocketEventListener webSocketEventListener;
////
////    public void broadcastOnlineUsers() {
////        messagingTemplate.convertAndSend("/topic/online-users", webSocketEventListener.getOnlineUsers());
////    }
////}
//@Component
//public class WebSocketNotifier {
//
//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//
//    public void broadcastOnlineUsers(Set<String> onlineUsers) {
//        messagingTemplate.convertAndSend("/topic/online-users", onlineUsers);
//    }
//}
