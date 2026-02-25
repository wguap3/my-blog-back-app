package blog.service;

import blog.dto.*;
import blog.mapper.PostMapper;
import blog.model.Post;
import blog.repository.CommentRepository;
import blog.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostMapper postMapper;

    @Override
    public PostResponseDto getPosts(String search, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<String> tags = new ArrayList<>();
        StringBuilder titleBuilder = new StringBuilder();

        if (search != null && !search.isBlank()) {
            String[] words = search.split("\\s+");
            for (String word : words) {
                if (word.startsWith("#")) {
                    tags.add(word.substring(1));
                } else if (!word.isBlank()) {
                    if (titleBuilder.length() > 0) titleBuilder.append(" ");
                    titleBuilder.append(word);
                }
            }
        }

        String titleSearch = titleBuilder.toString();

        Page<Post> page;

        if (!tags.isEmpty() && !titleSearch.isEmpty()) {
            page = postRepository.searchByTitleAndTags(titleSearch, tags, pageable);
        } else if (!tags.isEmpty()) {
            page = postRepository.findByTags(tags, pageable);
        } else if (!titleSearch.isEmpty()) {
            page = postRepository.findByTitleContainingIgnoreCase(titleSearch, pageable);
        } else {
            page = postRepository.findAll(pageable);
        }

        List<PostPreviewDto> posts = page.getContent().stream()
                .map(postMapper::mapPostToPostPreviewDto)
                .collect(Collectors.toList());

        PostResponseDto response = new PostResponseDto();
        response.setPosts(posts);
        response.setHasPrev(page.hasPrevious());
        response.setHasNext(page.hasNext());
        response.setLastPage(page.getTotalPages());

        return response;
    }

    @Override
    public PostDto getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id " + id));
        return postMapper.mapPostToPostDto(post);
    }


    @Override
    @Transactional
    public PostDto createPost(PostRequestDto dto) {
        Post post = postMapper.mapPostRequestDtoToPost(dto);

        Post saved = postRepository.saveAndFlush(post);


        return postMapper.mapPostToPostDto(saved);
    }

    @Override
    @Transactional
    public PostDto updatePost(Long id, PostRequestDto dto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id " + id));

        postMapper.updatePostFromDto(dto, post);
        Post updated = postRepository.save(post);
        return postMapper.mapPostToPostDto(updated);
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id " + id));


        commentRepository.deleteByPostId(id);

        postRepository.delete(post);
    }

    @Override
    @Transactional
    public Integer likePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id " + id));

        post.setLikesCount(post.getLikesCount() + 1);
        postRepository.save(post);
        return post.getLikesCount();
    }

    @Override
    @Transactional
    public void uploadImage(Long id, MultipartFile image) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id " + id));
        try {
            post.setImage(image.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image file", e);
        }
        postRepository.saveAndFlush(post);
    }

    @Override
    public byte[] getImage(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id " + id));
        return post.getImage();
    }

    @Override
    public List<CommentDto> getCommentsByPostId(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new RuntimeException("Post not found with id " + postId);
        }

        return commentRepository.findByPostId(postId).stream()
                .map(comment -> {
                    CommentDto dto = new CommentDto();
                    dto.setId(comment.getId());
                    dto.setText(comment.getText());
                    return dto;
                })
                .collect(Collectors.toList());
    }

}
