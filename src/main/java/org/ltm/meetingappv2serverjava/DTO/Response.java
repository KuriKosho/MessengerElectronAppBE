package org.ltm.meetingappv2serverjava.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Response<T> {
    private boolean success;
    private T data;
}
