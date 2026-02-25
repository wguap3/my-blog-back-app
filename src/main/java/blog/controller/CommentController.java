package blog.controller;

import blog.dto.CommentDto;
import blog.dto.CommentRequestDto;
import blog.service.CommentService;
import blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable String postId) {
        if ("undefined".equals(postId) || "null".equals(postId)) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        try {
            Long id = Long.parseLong(postId);
            List<CommentDto> comments = postService.getCommentsByPostId(id);
            return ResponseEntity.ok(comments);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> getComment(@PathVariable Long postId, @PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getComment(postId, commentId));
    }

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@PathVariable Long postId,
                                                    @RequestBody CommentRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(postId, dto));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long postId,
                                                    @PathVariable Long commentId,
                                                    @RequestBody CommentRequestDto dto) {
        return ResponseEntity.ok(commentService.updateComment(postId, commentId, dto));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long postId,
                                              @PathVariable Long commentId) {
        commentService.deleteComment(postId, commentId);
        return ResponseEntity.ok().build();
    }
}
