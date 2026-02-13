package blog.service;

import blog.dto.*;
import blog.mapper.PostMapper;
import blog.model.Post;
import blog.repository.CommentRepository;
import blog.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    void getPosts_WithTagsAndTitle_ShouldCallCorrectRepositoryMethod() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> page = new PageImpl<>(Collections.singletonList(new Post()));

        when(postRepository.searchByTitleAndTags(eq("spring"), eq(List.of("java")), any(Pageable.class)))
                .thenReturn(page);
        when(postMapper.mapPostToPostPreviewDto(any())).thenReturn(new PostPreviewDto());

        PostResponseDto result = postService.getPosts("#java spring", 1, 10);

        assertThat(result.getPosts()).hasSize(1);
        verify(postRepository).searchByTitleAndTags(eq("spring"), eq(List.of("java")), any());
    }

    @Test
    void getPost_IdExists_ShouldReturnDto() {

        Post post = new Post();
        post.setId(1L);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postMapper.mapPostToPostDto(post)).thenReturn(new PostDto());

        PostDto result = postService.getPost(1L);

        assertThat(result).isNotNull();
        verify(postRepository).findById(1L);
    }

    @Test
    void createPost_ValidDto_ShouldSaveAndReturnDto() {
        PostRequestDto request = new PostRequestDto();
        Post post = new Post();
        when(postMapper.mapPostRequestDtoToPost(request)).thenReturn(post);
        when(postRepository.saveAndFlush(post)).thenReturn(post);
        when(postMapper.mapPostToPostDto(post)).thenReturn(new PostDto());

        PostDto result = postService.createPost(request);

        assertThat(result).isNotNull();
        verify(postRepository).saveAndFlush(any(Post.class));
    }

    @Test
    void updatePost_IdExists_ShouldUpdateAndSave() {
        PostRequestDto dto = new PostRequestDto();
        Post post = new Post();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.mapPostToPostDto(post)).thenReturn(new PostDto());

        postService.updatePost(1L, dto);

        verify(postMapper).updatePostFromDto(eq(dto), eq(post));
        verify(postRepository).save(post);
    }

    @Test
    void deletePost_IdExists_ShouldDeleteCommentsAndPost() {
        Post post = new Post();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.deletePost(1L);

        verify(commentRepository).deleteByPostId(1L);
        verify(postRepository).delete(post);
    }

    @Test
    void likePost_ShouldIncrementLikes() {

        Post post = new Post();
        post.setLikesCount(10);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Integer newLikes = postService.likePost(1L);

        assertThat(newLikes).isEqualTo(11);
        verify(postRepository).save(post);
    }

    @Test
    void uploadImage_ValidFile_ShouldSetBytes() throws IOException {
        Post post = new Post();
        MockMultipartFile file = new MockMultipartFile("img", "test.jpg", "image/jpeg", "content".getBytes());
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.uploadImage(1L, file);


        assertThat(post.getImage()).isEqualTo("content".getBytes());
        verify(postRepository).saveAndFlush(post);
    }

    @Test
    void getImage_ShouldReturnBytes() {
        Post post = new Post();
        post.setImage("data".getBytes());
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));


        byte[] result = postService.getImage(1L);


        assertThat(result).isEqualTo("data".getBytes());
    }

    @Test
    void getCommentsByPostId_PostExists_ShouldReturnComments() {

        when(postRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findByPostId(1L)).thenReturn(Collections.emptyList());


        List<CommentDto> result = postService.getCommentsByPostId(1L);


        assertThat(result).isEmpty();
        verify(commentRepository).findByPostId(1L);
    }
}
