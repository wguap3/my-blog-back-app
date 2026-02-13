package blog.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(nullable = false)
    private String title;
    private String text;
    @ElementCollection
    @CollectionTable(name = "post_tag", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();
    @Column(name = "likes_count", nullable = false)
    private Integer likesCount = 0;
    @Column(name = "comments_count")
    private Integer commentsCount = 0;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    //@Lob
    private byte[] image;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
