package blog.mapper;

import blog.dto.CommentDto;
import blog.dto.CommentRequestDto;
import blog.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(source = "post.id", target = "postId")
    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Comment toComment(CommentRequestDto dto);
}

