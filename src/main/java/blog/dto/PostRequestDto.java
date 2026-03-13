package blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PostRequestDto {
    @NotBlank
    @Size(max = 200)
    private String title;
    @NotBlank
    private String text;
    @NotNull
    @Size(max = 20)
    private List<String> tags;
}
