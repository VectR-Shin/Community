package io.github.vectr.community.community.entity.post;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(
        name = "post_view",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_post_view_post_uuid",
                        columnNames = {"post_id", "uuid"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false, length = 50)
    private String uuid;

    @Column(name = "last_viewed_at", nullable = false)
    private Instant lastViewedAt;

    public PostView(Post post, String uuid) {
        this.post = Objects.requireNonNull(post);
        this.uuid = Objects.requireNonNull(uuid);
        this.lastViewedAt = Instant.now();
    }
}