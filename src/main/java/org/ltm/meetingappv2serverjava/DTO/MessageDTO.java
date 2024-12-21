package org.ltm.meetingappv2serverjava.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@RequiredArgsConstructor
public class MessageDTO {
//    senderId,
//    receiverId,
//    content,
//    timestamp: new Date().toISOString()
    private String senderId;
    private String receiverId;
    private String content;
    private String timestamp;
}
