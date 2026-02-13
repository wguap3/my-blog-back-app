package blog.service;

import blog.dto.CommentDto;
import blog.dto.CommentRequestDto;
import blog.mapper.CommentMapper;
import blog.model.Comment;
import blog.model.Post;
import blog.repository.CommentRepository;
import blog.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void getComments_PostExists_ReturnsList() {
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.of(new Post()));
        when(commentRepository.findByPostId(postId)).thenReturn(List.of(new Comment(), new Comment()));
        when(commentMapper.toCommentDto(any())).thenReturn(new CommentDto());


        List<CommentDto> result = commentService.getComments(postId);

        assertThat(result).hasSize(2);
        verify(commentRepository).findByPostId(postId);
    }

    @Test
    void getComment_Exists_ReturnsDto() {
        Long postId = 1L;
        Long commentId = 10L;
        Comment comment = new Comment();
        when(commentRepository.findByIdAndPostId(commentId, postId)).thenReturn(Optional.of(comment));
        when(commentMapper.toCommentDto(comment)).thenReturn(new CommentDto());

        CommentDto result = commentService.getComment(postId, commentId);

        assertThat(result).isNotNull();
        verify(commentRepository).findByIdAndPostId(commentId, postId);
    }

    @Test
    void addComment_ShouldSaveAndIncrementPostCounter() {
        Long postId = 1L;
        Post post = new Post();
        post.setCommentsCount(5);

        CommentRequestDto dto = new CommentRequestDto();
        dto.setText("New Comment");

        Comment savedComment = new Comment();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);
        when(commentMapper.toCommentDto(savedComment)).thenReturn(new CommentDto());


        commentService.addComment(postId, dto);

        assertThat(post.getCommentsCount()).isEqualTo(6); // Проверяем логику инкремента
        verify(commentRepository).save(any(Comment.class));
        verify(postRepository).save(post); // Проверяем, что пост сохранился с новым числом
    }

    @Test
    void updateComment_Exists_UpdatesText() {

        Long postId = 1L;
        Long commentId = 10L;
        Comment comment = new Comment();
        comment.setText("Old text");

        CommentRequestDto dto = new CommentRequestDto();
        dto.setText("Updated text");

        when(commentRepository.findByIdAndPostId(commentId, postId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);


        commentService.updateComment(postId, commentId, dto);

        assertThat(comment.getText()).isEqualTo("Updated text");
        verify(commentRepository).save(comment);
    }

    @Test
    void deleteComment_ShouldRemoveAndDecrementPostCounter() {
        Long postId = 1L;
        Long commentId = 10L;

        Post post = new Post();
        post.setCommentsCount(3);

        Comment comment = new Comment();
        comment.setPost(post);

        when(commentRepository.findByIdAndPostId(commentId, postId)).thenReturn(Optional.of(comment));

        commentService.deleteComment(postId, commentId);

        assertThat(post.getCommentsCount()).isEqualTo(2); // Проверяем декремент
        verify(commentRepository).delete(comment);
        verify(postRepository).save(post);
    }

    @Test
    void getComments_PostNotFound_ThrowsException() {
        Long postId = 999L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.getComments(postId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Post not found");
    }
}

