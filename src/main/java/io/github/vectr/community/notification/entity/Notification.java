package io.github.vectr.community.notification.entity;

import io.github.vectr.community.common.entity.BaseCreatedAtEntity;
import io.github.vectr.community.notification.enums.NotificationType;
import io.github.vectr.community.notification.enums.TargetType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 30)
    private NotificationType notificationType;

    @Column(nullable = false, length = 200)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", length = 20)
    private TargetType targetType;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    public Notification(
            Long receiverId,
            NotificationType notificationType,
            String content,
            TargetType targetType,
            Long targetId
    ) {
        this.receiverId = Objects.requireNonNull(receiverId);
        this.notificationType = Objects.requireNonNull(notificationType);
        this.content = Objects.requireNonNull(content);

        if ((targetType == null) != (targetId == null)) {
            throw new IllegalArgumentException(
                    "targetType과 targetId는 모두 null이거나 모두 값이 있어야 합니다."
            );
        }

        this.targetType = targetType;
        this.targetId = targetId;
    }

    public void readNotification() {
        this.isRead = true;
    }
}