package blog.controller;

import blog.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PostControllerIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {

        jdbcTemplate.execute("DELETE FROM post_tag");
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("DELETE FROM posts");

        jdbcTemplate.execute("""
                INSERT INTO posts (id, title, text, likes_count, comments_count) 
                VALUES (1, 'First Post', 'Some long text here', 10, 0)
                """);

        jdbcTemplate.execute("ALTER TABLE posts ALTER COLUMN id RESTART WITH 2");

        jdbcTemplate.execute("INSERT INTO post_tag (post_id, tag) VALUES (1, 'java')");


    }

    @Test
    void getPosts_withSearch_returnsFilteredResult() throws Exception {
        mockMvc.perform(get("/api/posts")
                        .param("search", "First")
                        .param("pageNumber", "1")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts", hasSize(1)))
                .andExpect(jsonPath("$.posts[0].title").value("First Post"));
    }

    @Test
    void createPost_savesDataInDb() throws Exception {
        String json = """
                {
                    "title": "New Tech Post",
                    "text": "Detailed content about coding"
                }
                """;

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Tech Post"));

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM posts", Integer.class);
        assertThat(count).isEqualTo(2);
    }

    @Test
    void likePost_updatesDatabaseField() throws Exception {
        mockMvc.perform(post("/api/posts/{id}/likes", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("11"));

        Long likes = jdbcTemplate.queryForObject("SELECT likes_count FROM posts WHERE id = 1", Long.class);
        assertThat(likes).isEqualTo(11L);
    }

    @Test
    void uploadImage_savesBytesToPost() throws Exception {
        byte[] imageBytes = new byte[]{10, 20, 30, 40};
        MockMultipartFile file = new MockMultipartFile("image", "blog.jpg", "image/jpeg", imageBytes);

        mockMvc.perform(multipart("/api/posts/{id}/image", 1L)
                        .file(file)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/posts/{id}/image", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(imageBytes));
    }

    @Test
    void deletePost_removesFromAllTables() throws Exception {
        mockMvc.perform(delete("/api/posts/{id}", 1L))
                .andExpect(status().isOk());


        Integer tagCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM post_tag WHERE post_id = 1", Integer.class);
        assertThat(tagCount).isEqualTo(0);
    }

    @Test
    void updatePost_changesFieldsInDb() throws Exception {
        String updatedJson = """
                {
                    "title": "Updated Title",
                    "text": "Updated content"
                }
                """;

        mockMvc.perform(put("/api/posts/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));

        String dbTitle = jdbcTemplate.queryForObject("SELECT title FROM posts WHERE id = 1", String.class);
        assertThat(dbTitle).isEqualTo("Updated Title");
    }
}