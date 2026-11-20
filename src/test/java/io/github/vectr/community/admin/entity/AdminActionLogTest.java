package io.github.vectr.community.admin.entity;

import io.github.vectr.community.admin.enums.ActionType;
import io.github.vectr.community.admin.enums.TargetType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AdminActionLogTest {

    @Test
    @DisplayName("정상적으로 관리자 활동 로그를 생성한다.")
    void createAdminActionLog() {
        // given
        Long adminId = 1L;
        Long targetId = 2L;
        TargetType targetType = TargetType.MEMBER;
        ActionType actionType = ActionType.SUSPEND_MEMBER;

        // when
        AdminActionLog log = new AdminActionLog(
                adminId,
                targetId,
                targetType,
                actionType
        );

        // then
        assertThat(log.getAdminId()).isEqualTo(adminId);
        assertThat(log.getTargetId()).isEqualTo(targetId);
        assertThat(log.getTargetType()).isEqualTo(targetType);
        assertThat(log.getActionType()).isEqualTo(actionType);
    }

    @Test
    @DisplayName("대상 ID가 null이어도 관리자 활동 로그를 생성할 수 있다.")
    void createAdminActionLogWithNullTargetId() {
        // given
        Long adminId = 1L;
        TargetType targetType = TargetType.MEMBER;
        ActionType actionType = ActionType.SUSPEND_MEMBER;

        // when
        AdminActionLog log = new AdminActionLog(
                adminId,
                null,
                targetType,
                actionType
        );

        // then
        assertThat(log.getAdminId()).isEqualTo(adminId);
        assertThat(log.getTargetId()).isNull();
        assertThat(log.getTargetType()).isEqualTo(targetType);
        assertThat(log.getActionType()).isEqualTo(actionType);
    }

    @Test
    @DisplayName("관리자 ID가 null이면 예외가 발생한다.")
    void createAdminActionLogWithNullAdminId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new AdminActionLog(
                        null,
                        2L,
                        TargetType.MEMBER,
                        ActionType.SUSPEND_MEMBER
                ));
    }

    @Test
    @DisplayName("대상 유형이 null이면 예외가 발생한다.")
    void createAdminActionLogWithNullTargetType() {
        assertThatNullPointerException()
                .isThrownBy(() -> new AdminActionLog(
                        1L,
                        2L,
                        null,
                        ActionType.SUSPEND_MEMBER
                ));
    }

    @Test
    @DisplayName("행위 유형이 null이면 예외가 발생한다.")
    void createAdminActionLogWithNullActionType() {
        assertThatNullPointerException()
                .isThrownBy(() -> new AdminActionLog(
                        1L,
                        2L,
                        TargetType.MEMBER,
                        null
                ));
    }
}