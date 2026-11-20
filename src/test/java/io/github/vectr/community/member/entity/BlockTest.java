package io.github.vectr.community.member.entity;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.member.enums.MemberProvider;
import io.github.vectr.community.member.enums.MemberRole;
import io.github.vectr.community.member.exception.BlockErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

class BlockTest {

    @Test
    @DisplayName("정상적으로 차단을 생성한다.")
    void createBlock() {
        // given
        Member blocker = createDefaultMember();
        Member blocked = createDefaultMember();

        // when
        Block block = new Block(blocker, blocked);

        // then
        assertThat(block.getBlocker()).isSameAs(blocker);
        assertThat(block.getBlocked()).isSameAs(blocked);
    }

    @Test
    @DisplayName("차단자가 null이면 예외가 발생한다.")
    void createBlockWithNullBlocker() {
        // given
        Member blocked = createDefaultMember();

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> new Block(null, blocked));
    }

    @Test
    @DisplayName("차단 대상이 null이면 예외가 발생한다.")
    void createBlockWithNullBlocked() {
        // given
        Member blocker = createDefaultMember();

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> new Block(blocker, null));
    }

    @Test
    @DisplayName("동일한 Member 객체를 차단하려 하면 예외가 발생한다.")
    void createBlockWithSameMember() {
        // given
        Member member = createDefaultMember();

        // when & then
        assertThatThrownBy(() -> new Block(member, member))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(BlockErrorCode.SELF_BLOCK_NOT_ALLOWED);
    }

    @Test
    @DisplayName("ID가 같은 Member 객체를 차단하려 하면 예외가 발생한다.")
    void createBlockWithSameMemberId() {
        // given
        Member blocker = createDefaultMember();
        Member blocked = createDefaultMember();

        ReflectionTestUtils.setField(blocker, "id", 1L);
        ReflectionTestUtils.setField(blocked, "id", 1L);

        // when & then
        assertThatThrownBy(() -> new Block(blocker, blocked))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(BlockErrorCode.SELF_BLOCK_NOT_ALLOWED);
    }

    private Member createDefaultMember() {
        return new Member(
                "keycloak-sub",
                "test@example.com",
                MemberRole.USER
        );
    }
}