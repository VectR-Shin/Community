package io.github.vectr.community.notification.repository;

import io.github.vectr.community.notification.entity.SystemNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemNotificationRepository extends JpaRepository<SystemNotification, Integer> {

}
