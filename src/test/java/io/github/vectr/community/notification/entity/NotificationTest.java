package io.github.vectr.community.notification.entity;

import io.github.vectr.community.notification.enums.NotificationType;
import io.github.vectr.community.notification.enums.TargetType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class NotificationTest {

    @Test
    @DisplayName("정상적으로 알림을 생성한다.")
    void createNotification() {
        // given
        Long receiverId = 1L;
        NotificationType notificationType = NotificationType.COMMENT;
        String content = "새로운 댓글이 작성되었습니다.";
        TargetType targetType = TargetType.POST;
        Long targetId = 10L;

        // when
        Notification notification = new Notification(
                receiverId,
                notificationType,
                content,
                targetType,
                targetId
        );

        // then
        assertThat(notification.getReceiverId()).isEqualTo(receiverId);
        assertThat(notification.getNotificationType()).isEqualTo(notificationType);
        assertThat(notification.getContent()).isEqualTo(content);
        assertThat(notification.getTargetType()).isEqualTo(targetType);
        assertThat(notification.getTargetId()).isEqualTo(targetId);
        assertThat(notification.isRead()).isFalse();
    }

    @Test
    @DisplayName("대상 정보가 모두 null이어도 알림을 생성할 수 있다.")
    void createNotificationWithoutTargetInfo() {
        // given
        Long receiverId = 1L;
        NotificationType notificationType = NotificationType.COMMENT;
        String content = "새로운 알림이 도착했습니다.";

        // when
        Notification notification = new Notification(
                receiverId,
                notificationType,
                content,
                null,
                null
        );

        // then
        assertThat(notification.getReceiverId()).isEqualTo(receiverId);
        assertThat(notification.getTargetType()).isNull();
        assertThat(notification.getTargetId()).isNull();
    }

    @Test
    @DisplayName("대상 유형만 존재하면 예외가 발생한다.")
    void createNotificationWithNullTargetId() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Notification(
                        1L,
                        NotificationType.COMMENT,
                        "새로운 댓글이 작성되었습니다.",
                        TargetType.POST,
                        null
                ));
    }

    @Test
    @DisplayName("대상 ID만 존재하면 예외가 발생한다.")
    void createNotificationWithNullTargetType() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Notification(
                        1L,
                        NotificationType.COMMENT,
                        "새로운 댓글이 작성되었습니다.",
                        null,
                        10L
                ));
    }

    @Test
    @DisplayName("수신자 ID가 null이면 예외가 발생한다.")
    void createNotificationWithNullReceiverId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Notification(
                        null,
                        NotificationType.COMMENT,
                        "새로운 댓글이 작성되었습니다.",
                        null,
                        null
                ));
    }

    @Test
    @DisplayName("알림 유형이 null이면 예외가 발생한다.")
    void createNotificationWithNullNotificationType() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Notification(
                        1L,
                        null,
                        "새로운 댓글이 작성되었습니다.",
                        null,
                        null
                ));
    }

    @Test
    @DisplayName("알림 내용이 null이면 예외가 발생한다.")
    void createNotificationWithNullContent() {
        assertThatNullPointerException()
                .isThrownBy(() -> new Notification(
                        1L,
                        NotificationType.COMMENT,
                        null,
                        null,
                        null
                ));
    }

    @Test
    @DisplayName("알림을 읽으면 읽음 상태로 변경된다.")
    void readNotification() {
        // given
        Notification notification = new Notification(
                1L,
                NotificationType.COMMENT,
                "새로운 댓글이 작성되었습니다.",
                null,
                null
        );

        // when
        notification.readNotification();

        // then
        assertThat(notification.isRead()).isTrue();
    }

    @Test
    @DisplayName("이미 읽은 알림을 다시 읽어도 읽음 상태가 유지된다.")
    void readAlreadyReadNotification() {
        // given
        Notification notification = new Notification(
                1L,
                NotificationType.COMMENT,
                "새로운 댓글이 작성되었습니다.",
                null,
                null
        );

        notification.readNotification();

        // when
        notification.readNotification();

        // then
        assertThat(notification.isRead()).isTrue();
    }
}