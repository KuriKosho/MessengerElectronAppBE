package org.ltm.meetingappv2serverjava.model;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;


@Document(collection = "messages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {
    @Id
    @MongoId
    private String id;
    private String senderId;
    private String receiverId;
    private String content;
    private LocalDateTime timestamp ;
}
