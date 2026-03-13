package blog.controller;

import blog.dto.CommentRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CommentControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        jdbcTemplate.execute("DELETE FROM post_tag");
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("DELETE FROM posts");

        jdbcTemplate.execute(
                "INSERT INTO posts (id, title, text, likes_count, comments_count) " +
                        "VALUES (1, 'Parent Post', 'Content', 0, 0)"
        );
        jdbcTemplate.execute("ALTER TABLE posts ALTER COLUMN id RESTART WITH 2");

        jdbcTemplate.execute("INSERT INTO comments (id, post_id, text) VALUES (10, 1, 'Initial Comment')");
        jdbcTemplate.execute("ALTER TABLE comments ALTER COLUMN id RESTART WITH 11");
    }

    @Test
    void getComments_validPostId_returnsList() throws Exception {
        mockMvc.perform(get("/api/posts/{postId}/comments", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].text").value("Initial Comment"));
    }

    @Test
    void getComments_undefined_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/posts/{postId}/comments", "undefined"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void createComment_savesToDb() throws Exception {
        CommentRequestDto dto = new CommentRequestDto();
        dto.setText("New Comment");
        dto.setPostId(1L);

        mockMvc.perform(post("/api/posts/{postId}/comments", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value("New Comment"));

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM comments", Integer.class);
        assertThat(count).isEqualTo(2);
    }

    @Test
    void updateComment_updatesText() throws Exception {
        CommentRequestDto dto = new CommentRequestDto();
        dto.setText("Updated Text");

        mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", 1L, 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        String text = jdbcTemplate.queryForObject("SELECT text FROM comments WHERE id = 10", String.class);
        assertThat(text).isEqualTo("Updated Text");
    }

    @Test
    void deleteComment_removesFromDb() throws Exception {
        mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", 1L, 10L))
                .andExpect(status().isOk());

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM comments WHERE id = 10", Integer.class);
        assertThat(count).isEqualTo(0);
    }
}