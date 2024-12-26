package org.ltm.meetingappv2serverjava.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Verify {
    private String email;
    private String code;
}
