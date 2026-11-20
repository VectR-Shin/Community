package io.github.vectr.community.community.entity.post;

import io.github.vectr.community.common.entity.BaseCreatedAtEntity;
import io.github.vectr.community.community.enums.ReactionType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(
        name = "post_reaction",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_post_reaction_member_post",
                        columnNames = {"member_id", "post_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostReaction extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false, length = 20)
    private ReactionType reactionType;

    public PostReaction(
            Long memberId,
            Post post,
            ReactionType reactionType
    ) {
        this.memberId = Objects.requireNonNull(memberId);
        this.post = Objects.requireNonNull(post);
        this.reactionType = Objects.requireNonNull(reactionType);
    }
}