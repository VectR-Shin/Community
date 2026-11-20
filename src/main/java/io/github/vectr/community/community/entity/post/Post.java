package io.github.vectr.community.community.entity.post;

import io.github.vectr.community.common.entity.BaseTimeEntity;
import io.github.vectr.community.community.entity.board.Board;
import io.github.vectr.community.community.enums.PostType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 5000)
    private String content;

    @Column(name = "comment_count", nullable = false)
    private long commentCount = 0;

    @Column(name = "view_count", nullable = false)
    private long viewCount = 0;

    @Column(name = "like_count", nullable = false)
    private long likeCount = 0;

    @Column(name = "dislike_count", nullable = false)
    private long dislikeCount = 0;

    @Column(name = "is_popular", nullable = false)
    private boolean isPopular = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false, length = 20)
    private PostType postType;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public Post(
            Long memberId,
            Board board,
            String title,
            String content,
            PostType postType
    ) {
        this.memberId = Objects.requireNonNull(memberId);
        this.board = Objects.requireNonNull(board);
        this.title = Objects.requireNonNull(title);
        this.content = Objects.requireNonNull(content);
        this.postType = Objects.requireNonNull(postType);
    }

    public void changeBoard(Board board) {
        this.board = Objects.requireNonNull(board);
    }

    public void changeTitle(String title) {
        this.title = Objects.requireNonNull(title);
    }

    public void changeContent(String content) {
        this.content = Objects.requireNonNull(content);
    }

    public void changePostType(PostType postType) {
        this.postType = Objects.requireNonNull(postType);
    }

    public void changePopular(boolean isPopular) {
        this.isPopular = isPopular;
    }

    public void delete() {
        this.deletedAt = Instant.now();
    }

    public void restore() {
        this.deletedAt = null;
    }
}