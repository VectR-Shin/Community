package io.github.vectr.community.notification.service;

import io.github.vectr.community.notification.api.NotificationCommandService;
import io.github.vectr.community.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationCommandServiceImpl implements NotificationCommandService {

    private final NotificationRepository notificationRepository;


    // 특정 사용자의 모든 수신 알림 삭제
    @Override
    public void deleteAllNotificationByReceiverId(Long receiverId) {
        notificationRepository.deleteAllByReceiverId(receiverId);
    }
}
