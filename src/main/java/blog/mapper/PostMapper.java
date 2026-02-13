package blog.mapper;

import blog.dto.PostDto;
import blog.dto.PostPreviewDto;
import blog.dto.PostRequestDto;
import blog.model.Post;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    PostDto mapPostToPostDto(Post post);

    @Mapping(target = "text", expression = "java(truncateText(post.getText()))")
    PostPreviewDto mapPostToPostPreviewDto(Post post);

    List<PostPreviewDto> mapPostsToPostPreviewDtos(List<Post> posts);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "likesCount", constant = "0")
    @Mapping(target = "commentsCount", constant = "0")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "image", ignore = true)
    Post mapPostRequestDtoToPost(PostRequestDto postRequestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePostFromDto(PostRequestDto dto, @MappingTarget Post post);

    default String truncateText(String text) {
        if (text == null) return null;
        if (text.length() <= 128) return text;
        return text.substring(0, 128) + "…";
    }

}
