package io.github.vectr.community.member.entity;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.member.enums.MemberProvider;
import io.github.vectr.community.member.enums.MemberRole;
import io.github.vectr.community.member.enums.MemberSuspensionReason;
import io.github.vectr.community.member.exception.MemberSuspensionErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberSuspensionTest {

    private Member createDefaultMember() {
        return new Member(
                "keycloak-sub",
                "test@example.com",
                MemberRole.USER
        );
    }

    private MemberSuspension createDefaultSuspension(Instant endsAt) {
        Member member = createDefaultMember();
        Member createdBy = createDefaultMember();

        return new MemberSuspension(
                member,
                MemberSuspensionReason.OTHER,
                endsAt,
                createdBy
        );
    }

    @Test
    @DisplayName("MemberSuspension 객체를 정상적으로 생성한다.")
    void createMemberSuspension() {
        // given
        Member member = createDefaultMember();
        Member createdBy = createDefaultMember();
        MemberSuspensionReason reason = MemberSuspensionReason.OTHER;
        Instant endsAt = Instant.now().plusSeconds(3600);

        // when
        MemberSuspension suspension = new MemberSuspension(
                member, reason, endsAt, createdBy
        );

        // then
        assertThat(suspension.getMember()).isEqualTo(member);
        assertThat(suspension.getReason()).isEqualTo(reason);
        assertThat(suspension.getEndsAt()).isEqualTo(endsAt);
        assertThat(suspension.getCreatedBy()).isEqualTo(createdBy);
        assertThat(suspension.getLiftedBy()).isNull();
        assertThat(suspension.getLiftedAt()).isNull();
        assertThat(suspension.isLifted()).isFalse();
        assertThat(suspension.isExpired()).isFalse();
    }

    @Test
    @DisplayName("종료 시각이 null인 영구 정지를 생성한다.")
    void createPermanentSuspension() {
        // when
        MemberSuspension suspension = createDefaultSuspension(null);

        // then
        assertThat(suspension.getEndsAt()).isNull();
        assertThat(suspension.isExpired()).isFalse();
    }

    @Test
    @DisplayName("Member가 null이면 예외가 발생한다.")
    void createMemberSuspensionWithNullMember() {
        assertThatThrownBy(() -> new MemberSuspension(
                null,
                MemberSuspensionReason.OTHER,
                null,
                createDefaultMember()
        )).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Reason이 null이면 예외가 발생한다.")
    void createMemberSuspensionWithNullReason() {
        assertThatThrownBy(() -> new MemberSuspension(
                createDefaultMember(),
                null,
                null,
                createDefaultMember()
        )).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("CreatedBy가 null이면 예외가 발생한다.")
    void createMemberSuspensionWithNullCreatedBy() {
        assertThatThrownBy(() -> new MemberSuspension(
                createDefaultMember(),
                MemberSuspensionReason.OTHER,
                null,
                null
        )).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("정지 해제를 정상적으로 처리한다.")
    void lift() {
        // given
        MemberSuspension suspension = createDefaultSuspension(
                Instant.now().plusSeconds(3600)
        );
        Member liftedBy = createDefaultMember();

        // when
        suspension.lift(liftedBy);

        // then
        assertThat(suspension.getLiftedBy()).isEqualTo(liftedBy);
        assertThat(suspension.getLiftedAt()).isNotNull();
        assertThat(suspension.isLifted()).isTrue();
    }

    @Test
    @DisplayName("이미 해제된 정지를 다시 해제하려 하면 예외가 발생한다.")
    void liftAlreadyLiftedSuspension() {
        // given
        MemberSuspension suspension = createDefaultSuspension(
                Instant.now().plusSeconds(3600)
        );
        suspension.lift(createDefaultMember());

        // when & then
        assertThatThrownBy(() -> suspension.lift(createDefaultMember()))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(MemberSuspensionErrorCode.MEMBER_SUSPENSION_ALREADY_LIFTED);
    }

    @Test
    @DisplayName("만료된 정지를 해제하려 하면 예외가 발생한다.")
    void liftExpiredSuspension() {
        // given
        MemberSuspension suspension = createDefaultSuspension(
                Instant.now().minusSeconds(3600)
        );

        // when & then
        assertThatThrownBy(() -> suspension.lift(createDefaultMember()))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(MemberSuspensionErrorCode.MEMBER_NOT_SUSPENDED);
    }

    @Test
    @DisplayName("정지 해제자가 null이면 예외가 발생한다.")
    void liftWithNullMember() {
        // given
        MemberSuspension suspension = createDefaultSuspension(
                Instant.now().plusSeconds(3600)
        );

        // when & then
        assertThatThrownBy(() -> suspension.lift(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("종료 시각이 미래이면 만료되지 않은 것으로 판단한다.")
    void isNotExpired() {
        // given
        MemberSuspension suspension = createDefaultSuspension(
                Instant.now().plusSeconds(3600)
        );

        // when & then
        assertThat(suspension.isExpired()).isFalse();
    }

    @Test
    @DisplayName("종료 시각이 과거이면 만료된 것으로 판단한다.")
    void isExpired() {
        // given
        MemberSuspension suspension = createDefaultSuspension(
                Instant.now().minusSeconds(3600)
        );

        // when & then
        assertThat(suspension.isExpired()).isTrue();
    }
}