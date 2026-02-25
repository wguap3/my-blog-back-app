package blog.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PostRequestDto {
    private String title;
    private String text;
    private List<String> tags;
}
