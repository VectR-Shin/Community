package io.github.vectr.community.member.entity;

import io.github.vectr.community.common.entity.BaseTimeEntity;
import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.member.enums.MemberSuspensionReason;
import io.github.vectr.community.member.exception.MemberSuspensionErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Objects;

@Getter
@Entity
@Table(name = "member_suspension")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSuspension extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "member_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_member_suspension_member"
            )
    )
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private MemberSuspensionReason reason;

    @Column(name = "ends_at")
    private Instant endsAt;

    @Column(name = "lifted_at")
    private Instant liftedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "lifted_by",
            foreignKey = @ForeignKey(
                    name = "fk_member_suspension_lifted_by"
            )
    )
    private Member liftedBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "created_by",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_member_suspension_created_by"
            )
    )
    private Member createdBy;

    public MemberSuspension(
            Member member,
            MemberSuspensionReason reason,
            Instant endsAt,
            Member createdBy
    ) {
        this.member = Objects.requireNonNull(member);
        this.reason = Objects.requireNonNull(reason);
        this.endsAt = endsAt;
        this.createdBy = Objects.requireNonNull(createdBy);
    }

    public void lift(Member liftedBy) {
        Objects.requireNonNull(liftedBy);

        if (isLifted()) {
            throw new BusinessException(MemberSuspensionErrorCode.MEMBER_SUSPENSION_ALREADY_LIFTED);
        }

        if (isExpired()) {
            throw new BusinessException(MemberSuspensionErrorCode.MEMBER_NOT_SUSPENDED);
        }

        this.liftedBy = liftedBy;
        this.liftedAt = Instant.now();
    }

    public boolean isLifted() {
        return liftedAt != null;
    }

    public boolean isExpired() {
        return endsAt != null && !Instant.now().isBefore(endsAt);
    }
}
