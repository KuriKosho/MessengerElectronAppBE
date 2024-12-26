package org.ltm.meetingappv2serverjava.DTO;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FileUpload {
    String senderId;
    String receiverId;
    MultipartFile file;
}
