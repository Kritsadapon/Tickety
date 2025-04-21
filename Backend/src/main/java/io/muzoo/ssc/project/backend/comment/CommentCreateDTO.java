package io.muzoo.ssc.project.backend.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateDTO {
    private Long postId;
    private String comment;
}