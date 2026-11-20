package io.github.vectr.community.notification.entity;

import io.github.vectr.community.common.entity.BaseCreatedAtEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "system_notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SystemNotification extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String content;

    public SystemNotification(String content) {
        this.content = Objects.requireNonNull(content);
    }
}