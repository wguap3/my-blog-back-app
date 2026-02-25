package blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDto {
    private Long id;
    private String text;

    @JsonProperty("postId")
    private Long postId;
}
