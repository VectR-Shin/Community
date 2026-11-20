package io.github.vectr.community.community.entity.board;

import io.github.vectr.community.common.entity.BaseTimeEntity;
import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.community.enums.BoardSuspensionReason;
import io.github.vectr.community.community.exception.BoardSuspensionErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "board_member_suspension")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardMemberSuspension extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private BoardSuspensionReason reason;

    @Column(name = "ends_at")
    private Instant endsAt;

    @Column(name = "lifted_by")
    private Long liftedBy;

    @Column(name = "lifted_at")
    private Instant liftedAt;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    public BoardMemberSuspension(
            Board board,
            Long memberId,
            BoardSuspensionReason reason,
            Instant endsAt,
            Long createdBy
    ) {
        this.board = Objects.requireNonNull(board);
        this.memberId = Objects.requireNonNull(memberId);
        this.reason = Objects.requireNonNull(reason);
        this.endsAt = endsAt;
        this.createdBy = Objects.requireNonNull(createdBy);
    }

    public void lift(Long liftedBy) {
        if (isLifted()) {
            throw new BusinessException(BoardSuspensionErrorCode.MEMBER_BOARD_SUSPENSION_ALREADY_LIFTED);
        }

        if (isExpired()) {
            throw new BusinessException(BoardSuspensionErrorCode.MEMBER_NOT_BOARD_SUSPENDED);
        }

        this.liftedBy = Objects.requireNonNull(liftedBy);
        this.liftedAt = Instant.now();
    }

    public boolean isLifted() {
        return liftedAt != null;
    }

    public boolean isExpired() {
        return endsAt != null && !Instant.now().isBefore(endsAt);
    }
}