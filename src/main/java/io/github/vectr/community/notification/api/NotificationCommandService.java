package io.github.vectr.community.notification.api;

public interface NotificationCommandService {
    // 특정 사용자의 모든 수신 알림 삭제
    void deleteAllNotificationByReceiverId(Long receiverId);
}
