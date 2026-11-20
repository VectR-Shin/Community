package io.github.vectr.community.notification.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SystemNotificationTest {

    @Test
    @DisplayName("정상적으로 시스템 알림을 생성한다.")
    void createSystemNotification() {
        // given
        String content = "시스템 점검이 예정되어 있습니다.";

        // when
        SystemNotification notification = new SystemNotification(content);

        // then
        assertThat(notification.getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("알림 내용이 null이면 예외가 발생한다.")
    void createSystemNotificationWithNullContent() {
        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> new SystemNotification(null));
    }
}