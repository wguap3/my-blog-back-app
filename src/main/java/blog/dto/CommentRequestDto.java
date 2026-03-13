package blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CommentRequestDto {
    private Long id;
    @NotBlank
    private String text;

    @JsonProperty("postId")
    @NotNull
    private Long postId;
}
