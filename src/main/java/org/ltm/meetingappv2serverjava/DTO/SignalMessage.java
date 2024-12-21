package org.ltm.meetingappv2serverjava.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SignalMessage {
    private String command;  // Loại thông báo (offer, answer, candidate, join, leave, stop)
    private String senderId; // ID của người gửi
    private String receiverId; // ID của người nhận (nếu có)
    private Object data; // Dữ liệu gửi kèm (SDP hoặc ICE Candidate)
}
