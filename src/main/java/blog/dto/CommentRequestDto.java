package blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentRequestDto {
    private String text;

    @JsonProperty("postId")
    private Long postId;
}
