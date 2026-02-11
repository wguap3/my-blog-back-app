package blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentDto {
    private Long id;

    private String text;

    @JsonProperty("postId")
    private Long postId;
}
