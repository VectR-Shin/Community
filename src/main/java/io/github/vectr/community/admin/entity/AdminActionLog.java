package io.github.vectr.community.admin.entity;

import io.github.vectr.community.admin.enums.ActionType;
import io.github.vectr.community.admin.enums.TargetType;
import io.github.vectr.community.common.entity.BaseCreatedAtEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "admin_action_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminActionLog extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @Column(name = "target_id")
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private TargetType targetType;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 30)
    private ActionType actionType;

    public AdminActionLog(
            Long adminId,
            Long targetId,
            TargetType targetType,
            ActionType actionType
    ) {
        this.adminId = Objects.requireNonNull(adminId);
        this.targetId = targetId;
        this.targetType = Objects.requireNonNull(targetType);
        this.actionType = Objects.requireNonNull(actionType);
    }
}