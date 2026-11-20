package io.github.vectr.community.community.entity.comment;

import io.github.vectr.community.common.entity.BaseCreatedAtEntity;
import io.github.vectr.community.community.enums.ReactionType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(
        name = "comment_reaction",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_comment_reaction_member_comment",
                        columnNames = {"member_id", "comment_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentReaction extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false, length = 20)
    private ReactionType reactionType;

    public CommentReaction(Long memberId, Comment comment, ReactionType reactionType) {
        this.memberId = Objects.requireNonNull(memberId);
        this.comment = Objects.requireNonNull(comment);
        this.reactionType = Objects.requireNonNull(reactionType);
    }
}
