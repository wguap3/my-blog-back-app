package blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PostPreviewDto {
    @JsonProperty("id")
    private Long id;

    private String title;

    private String text;

    private List<String> tags;

    @JsonProperty("likesCount")
    private Integer likesCount;

    @JsonProperty("commentsCount")
    private Integer commentsCount;
}
