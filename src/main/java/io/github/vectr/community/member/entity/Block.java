package io.github.vectr.community.member.entity;

import io.github.vectr.community.common.entity.BaseCreatedAtEntity;
import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.member.exception.BlockErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Entity
@Table(
        name = "block",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_block_blocker_blocked",
                        columnNames = {"blocker_id", "blocked_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Block extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "blocker_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_block_blocker")
    )
    private Member blocker;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "blocked_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_block_blocked")
    )
    private Member blocked;

    public Block(Member blocker, Member blocked) {
        Objects.requireNonNull(blocker);
        Objects.requireNonNull(blocked);

        if (blocker == blocked ||
                (blocker.getId() != null && blocker.getId().equals(blocked.getId()))
        ) {
            throw new BusinessException(BlockErrorCode.SELF_BLOCK_NOT_ALLOWED);
        }

        this.blocker = blocker;
        this.blocked = blocked;
    }
}
