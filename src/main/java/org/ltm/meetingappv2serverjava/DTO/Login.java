package org.ltm.meetingappv2serverjava.DTO;

import lombok.*;
import org.springframework.web.bind.annotation.RequestBody;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Login {
    private String email;
    private String password;
}
