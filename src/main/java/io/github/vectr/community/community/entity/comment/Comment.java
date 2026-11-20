package io.github.vectr.community.community.entity.comment;

import io.github.vectr.community.common.entity.BaseTimeEntity;
import io.github.vectr.community.community.entity.post.Post;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(name = "like_count", nullable = false)
    private long likeCount = 0;

    @Column(name = "dislike_count", nullable = false)
    private long dislikeCount = 0;

    @Column(name = "is_popular", nullable = false)
    private boolean popular = false;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public Comment(Long memberId, Post post, Comment parent, String content) {
        this.memberId = Objects.requireNonNull(memberId);
        this.post = Objects.requireNonNull(post);
        this.parent = parent;
        this.content = Objects.requireNonNull(content);
    }

    public void changeContent(String content) {
        this.content = Objects.requireNonNull(content);
    }

    public void changePopular(boolean popular) {
        this.popular = popular;
    }

    public void delete() {
        this.deletedAt = Instant.now();
    }

    public void restore() {
        this.deletedAt = null;
    }
}