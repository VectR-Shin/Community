package io.github.vectr.community.log.entity;

import io.github.vectr.community.log.enums.ActionType;
import io.github.vectr.community.log.enums.ActorRole;
import io.github.vectr.community.log.enums.TargetType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ManagementLogTest {

    @Test
    void 생성자_호출시_필드가_정상적으로_초기화된다() {
        // given
        Long actorId = 1L;
        ActorRole actorRole = ActorRole.values()[0];
        Long targetId = 2L;
        TargetType targetType = TargetType.values()[0];
        ActionType actionType = ActionType.values()[0];

        // when
        ManagementLog managementLog = new ManagementLog(
                actorId,
                actorRole,
                targetId,
                targetType,
                actionType
        );

        // then
        assertThat(managementLog.getActorId()).isEqualTo(actorId);
        assertThat(managementLog.getActorRole()).isEqualTo(actorRole);
        assertThat(managementLog.getTargetId()).isEqualTo(targetId);
        assertThat(managementLog.getTargetType()).isEqualTo(targetType);
        assertThat(managementLog.getActionType()).isEqualTo(actionType);
    }

    @Test
    void actorId가_null이면_NullPointerException이_발생한다() {
        // given
        ActorRole actorRole = ActorRole.values()[0];
        Long targetId = 2L;
        TargetType targetType = TargetType.values()[0];
        ActionType actionType = ActionType.values()[0];

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> new ManagementLog(
                        null,
                        actorRole,
                        targetId,
                        targetType,
                        actionType
                ));
    }

    @Test
    void actorRole이_null이면_NullPointerException이_발생한다() {
        // given
        Long actorId = 1L;
        Long targetId = 2L;
        TargetType targetType = TargetType.values()[0];
        ActionType actionType = ActionType.values()[0];

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> new ManagementLog(
                        actorId,
                        null,
                        targetId,
                        targetType,
                        actionType
                ));
    }

    @Test
    void targetId가_null이면_NullPointerException이_발생한다() {
        // given
        Long actorId = 1L;
        ActorRole actorRole = ActorRole.values()[0];
        TargetType targetType = TargetType.values()[0];
        ActionType actionType = ActionType.values()[0];

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> new ManagementLog(
                        actorId,
                        actorRole,
                        null,
                        targetType,
                        actionType
                ));
    }

    @Test
    void targetType이_null이면_NullPointerException이_발생한다() {
        // given
        Long actorId = 1L;
        ActorRole actorRole = ActorRole.values()[0];
        Long targetId = 2L;
        ActionType actionType = ActionType.values()[0];

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> new ManagementLog(
                        actorId,
                        actorRole,
                        targetId,
                        null,
                        actionType
                ));
    }

    @Test
    void actionType이_null이면_NullPointerException이_발생한다() {
        // given
        Long actorId = 1L;
        ActorRole actorRole = ActorRole.values()[0];
        Long targetId = 2L;
        TargetType targetType = TargetType.values()[0];

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> new ManagementLog(
                        actorId,
                        actorRole,
                        targetId,
                        targetType,
                        null
                ));
    }
}