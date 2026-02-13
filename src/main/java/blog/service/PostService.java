package blog.service;

import blog.dto.CommentDto;
import blog.dto.PostDto;
import blog.dto.PostRequestDto;
import blog.dto.PostResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    PostResponseDto getPosts(String search, int pageNumber, int pageSize);

    PostDto getPost(Long id);

    List<CommentDto> getCommentsByPostId(Long postId);

    PostDto createPost(PostRequestDto dto);

    PostDto updatePost(Long id, PostRequestDto dto);

    void deletePost(Long id);

    Integer likePost(Long id);

    void uploadImage(Long id, MultipartFile image);

    byte[] getImage(Long id);
}
