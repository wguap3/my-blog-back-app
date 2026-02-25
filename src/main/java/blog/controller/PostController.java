package blog.controller;

import blog.dto.PostDto;
import blog.dto.PostRequestDto;
import blog.dto.PostResponseDto;
import blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<PostResponseDto> getPosts(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize
    ) {
        PostResponseDto response = postService.getPosts(search, pageNumber, pageSize);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPost(@PathVariable Long id) {
        PostDto post = postService.getPost(id);
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostRequestDto dto) {
        PostDto created = postService.createPost(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(@PathVariable Long id, @RequestBody PostRequestDto dto) {
        PostDto updated = postService.updatePost(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/likes")
    public ResponseEntity<Integer> likePost(@PathVariable Long id) {
        Integer likes = postService.likePost(id);
        return ResponseEntity.ok(likes);
    }

    @PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadImage(@PathVariable Long id, @RequestPart("image") MultipartFile image) {
        postService.uploadImage(id, image);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        byte[] image = postService.getImage(id);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }

}