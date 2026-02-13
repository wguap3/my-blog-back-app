package blog.repository;

import blog.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {


    Page<Post> findAll(Pageable pageable);

    Page<Post> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p JOIN p.tags t WHERE t IN :tags")
    Page<Post> findByTags(@Param("tags") List<String> tags, Pageable pageable);

    @Query("""
                SELECT DISTINCT p FROM Post p JOIN p.tags t
                WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))
                AND t IN :tags
            """)
    Page<Post> searchByTitleAndTags(@Param("title") String title,
                                    @Param("tags") List<String> tags,
                                    Pageable pageable);
}
