package blog.service;

import blog.dto.CommentDto;
import blog.dto.CommentRequestDto;

import java.util.List;

public interface CommentService {
    List<CommentDto> getComments(Long postId);


    CommentDto getComment(Long postId, Long commentId);


    CommentDto addComment(Long postId, CommentRequestDto dto);


    CommentDto updateComment(Long postId, Long commentId, CommentRequestDto dto);

    void deleteComment(Long postId, Long commentId);
}
