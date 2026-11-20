package io.github.vectr.community.notification.repository;

import io.github.vectr.community.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    // 특정 사용자의 모든 수신 알림 삭제
    void deleteAllByReceiverId(Long receiverId);
}
