package org.ltm.meetingappv2serverjava.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Register {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
}
