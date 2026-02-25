package blog.service;

import blog.dto.CommentDto;
import blog.dto.CommentRequestDto;
import blog.mapper.CommentMapper;
import blog.model.Comment;
import blog.model.Post;
import blog.repository.CommentRepository;
import blog.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;

    @Override
    public List<CommentDto> getComments(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id " + postId));


        List<Comment> comments = commentRepository.findByPostId(postId);


        return comments.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getComment(Long postId, Long commentId) {
        Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new RuntimeException(
                        "Comment not found with id " + commentId + " for post " + postId));

        return commentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto addComment(Long postId, CommentRequestDto dto) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id " + postId));


        Comment comment = new Comment();
        comment.setPost(post);
        comment.setText(dto.getText());
        comment.setCreatedAt(LocalDateTime.now());


        Comment saved = commentRepository.save(comment);


        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);

        return commentMapper.toCommentDto(saved);
    }


    @Override
    @Transactional
    public CommentDto updateComment(Long postId, Long commentId, CommentRequestDto dto) {
        Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new RuntimeException(
                        "Comment not found with id " + commentId + " for post " + postId));


        comment.setText(dto.getText());

        Comment updated = commentRepository.save(comment);
        return commentMapper.toCommentDto(updated);
    }

    @Override
    @Transactional
    public void deleteComment(Long postId, Long commentId) {
        Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new RuntimeException(
                        "Comment not found with id " + commentId + " for post " + postId));


        commentRepository.delete(comment);


        Post post = comment.getPost();
        post.setCommentsCount(Math.max(0, post.getCommentsCount() - 1));
        postRepository.save(post);
    }
}
