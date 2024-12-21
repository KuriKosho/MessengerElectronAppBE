package org.ltm.meetingappv2serverjava.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String id;
    private String username;
    private String email;
    private boolean isOnline;
    private String timeStamp;
    private String lastMessage;
}
