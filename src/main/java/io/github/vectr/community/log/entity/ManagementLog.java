package io.github.vectr.community.log.entity;

import io.github.vectr.community.log.enums.ActionType;
import io.github.vectr.community.log.enums.ActorRole;
import io.github.vectr.community.log.enums.TargetType;
import io.github.vectr.community.common.entity.BaseCreatedAtEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "admin_action_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ManagementLog extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "actor_id", nullable = false)
    private Long actorId;

    @Column(name = "actor_role", nullable = false)
    private ActorRole actorRole;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private TargetType targetType;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 30)
    private ActionType actionType;

    public ManagementLog(
            Long actorId,
            ActorRole actorRole,
            Long targetId,
            TargetType targetType,
            ActionType actionType
    ) {
        this.actorId = Objects.requireNonNull(actorId);
        this.actorRole = Objects.requireNonNull(actorRole);
        this.targetId = Objects.requireNonNull(targetId);
        this.targetType = Objects.requireNonNull(targetType);
        this.actionType = Objects.requireNonNull(actionType);
    }
}