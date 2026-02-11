package blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostResponseDto {
    private List<PostPreviewDto> posts;

    @JsonProperty("hasPrev")
    private Boolean hasPrev;

    @JsonProperty("hasNext")
    private Boolean hasNext;

    @JsonProperty("lastPage")
    private Integer lastPage;


}
