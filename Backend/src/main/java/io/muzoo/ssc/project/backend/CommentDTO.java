package io.muzoo.ssc.project.backend;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommentDTO {
    private Long commentId;
    private String comment;
    private String username;
    private LocalDateTime dateTime;
    private String profilePictureUrl;
}