
package io.github.vectr.community.member.repository;

import io.github.vectr.community.common.config.JpaAuditingTestConfig;
import io.github.vectr.community.member.dto.suspension.SuspensionResponseDTO;
import io.github.vectr.community.member.entity.Member;
import io.github.vectr.community.member.entity.MemberSuspension;
import io.github.vectr.community.member.enums.MemberRole;
import io.github.vectr.community.member.enums.MemberSuspensionReason;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import(JpaAuditingTestConfig.class)
class MemberSuspensionRepositoryTest {

    @Autowired
    private MemberSuspensionRepository memberSuspensionRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("현재 적용 중인 전역 밴 정보를 조회한다.")
    void findCurrentSuspension() {
        // given
        Member member = saveMember();

        Instant endsAt = Instant.now()
                .truncatedTo(ChronoUnit.MICROS)
                .plusSeconds(3600);

        MemberSuspension suspension = saveSuspension(
                member,
                endsAt
        );

        // 저장 이후 조회 기준 시각 생성
        Instant now = Instant.now()
                .plusSeconds(1)
                .truncatedTo(ChronoUnit.MICROS);

        // when
        Optional<SuspensionResponseDTO> result =
                memberSuspensionRepository.findCurrentSuspension(
                        member.getId(),
                        now
                );

        // then
        assertThat(result).isPresent();

        SuspensionResponseDTO response = result.get();

        assertThat(response.suspensionId()).isEqualTo(suspension.getId());
        assertThat(response.reason()).isEqualTo(suspension.getReason());
        assertThat(response.startedAt()).isNotNull();
        assertThat(response.endsAt()).isEqualTo(endsAt);
    }

    @Test
    @DisplayName("종료 일시가 없는 무제한 전역 밴도 조회한다.")
    void findCurrentSuspensionWithoutEndDate() {
        // given
        Member member = saveMember();

        Instant now = Instant.now().plusSeconds(10);

        saveSuspension(member, null);

        // when
        Optional<SuspensionResponseDTO> result =
                memberSuspensionRepository.findCurrentSuspension(
                        member.getId(),
                        now
                );

        // then
        assertThat(result).isPresent();
        assertThat(result.get().endsAt()).isNull();
    }

    @Test
    @DisplayName("아직 시작되지 않은 전역 밴은 조회하지 않는다.")
    void excludeNotStartedSuspension() {
        // given
        Member member = saveMember();

        saveSuspension(member, null);

        // 생성 시각보다 과거를 조회 기준 시각으로 설정
        Instant now = Instant.now().minusSeconds(60);

        // when
        Optional<SuspensionResponseDTO> result =
                memberSuspensionRepository.findCurrentSuspension(
                        member.getId(),
                        now
                );

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("종료된 전역 밴은 조회하지 않는다.")
    void excludeExpiredSuspension() {
        // given
        Member member = saveMember();

        Instant now = Instant.now().plusSeconds(10);
        Instant endsAt = now.minusSeconds(3600);

        saveSuspension(member, endsAt);

        // when
        Optional<SuspensionResponseDTO> result =
                memberSuspensionRepository.findCurrentSuspension(
                        member.getId(),
                        now
                );

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("관리자에 의해 해제된 전역 밴은 조회하지 않는다.")
    void excludeLiftedSuspension() {
        // given
        Member member = saveMember();

        Instant endsAt = Instant.now().plusSeconds(3600);

        MemberSuspension suspension = saveSuspension(
                member,
                endsAt
        );

        suspension.lift(member);
        memberSuspensionRepository.flush();

        // when
        Optional<SuspensionResponseDTO> result =
                memberSuspensionRepository.findCurrentSuspension(
                        member.getId(),
                        Instant.now()
                );

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("다른 회원의 전역 밴은 조회하지 않는다.")
    void excludeOtherMemberSuspension() {
        // given
        Member member = saveMember();
        Member otherMember = saveMember();

        saveSuspension(otherMember, null);

        // when
        Optional<SuspensionResponseDTO> result =
                memberSuspensionRepository.findCurrentSuspension(
                        member.getId(),
                        Instant.now().plusSeconds(10)
                );

        // then
        assertThat(result).isEmpty();
    }

    private Member saveMember() {
        String uniqueId = UUID.randomUUID().toString();

        Member member = new Member(
                "keycloak-" + uniqueId,
                uniqueId + "@example.com",
                MemberRole.USER
        );

        return memberRepository.saveAndFlush(member);
    }

    private MemberSuspension saveSuspension(
            Member member,
            Instant endsAt
    ) {
        MemberSuspension suspension = new MemberSuspension(
                member,
                MemberSuspensionReason.OTHER,
                endsAt,
                member
        );

        return memberSuspensionRepository.saveAndFlush(suspension);
    }

    @Test
    @DisplayName("현재 적용 중인 전역 밴이 있으면 true를 반환한다.")
    void existsCurrentSuspension() {
        // given
        Member member = saveMember();

        Instant endsAt = Instant.now()
                .truncatedTo(ChronoUnit.MICROS)
                .plusSeconds(3600);

        saveSuspension(member, endsAt);

        Instant now = Instant.now()
                .plusSeconds(1)
                .truncatedTo(ChronoUnit.MICROS);

        // when
        boolean result = memberSuspensionRepository.existsCurrentSuspension(
                member.getId(),
                now
        );

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("종료 일시가 없는 무제한 전역 밴도 true를 반환한다.")
    void existsCurrentSuspensionWithoutEndDate() {
        // given
        Member member = saveMember();

        saveSuspension(member, null);

        Instant now = Instant.now().plusSeconds(10);

        // when
        boolean result = memberSuspensionRepository.existsCurrentSuspension(
                member.getId(),
                now
        );

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("아직 시작되지 않은 전역 밴은 false를 반환한다.")
    void excludeNotStartedSuspensionFromExists() {
        // given
        Member member = saveMember();

        saveSuspension(member, null);

        Instant now = Instant.now().minusSeconds(60);

        // when
        boolean result = memberSuspensionRepository.existsCurrentSuspension(
                member.getId(),
                now
        );

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("종료된 전역 밴은 false를 반환한다.")
    void excludeExpiredSuspensionFromExists() {
        // given
        Member member = saveMember();

        Instant now = Instant.now().plusSeconds(10);
        Instant endsAt = now.minusSeconds(3600);

        saveSuspension(member, endsAt);

        // when
        boolean result = memberSuspensionRepository.existsCurrentSuspension(
                member.getId(),
                now
        );

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("관리자에 의해 해제된 전역 밴은 false를 반환한다.")
    void excludeLiftedSuspensionFromExists() {
        // given
        Member member = saveMember();

        Instant endsAt = Instant.now().plusSeconds(3600);

        MemberSuspension suspension = saveSuspension(member, endsAt);

        suspension.lift(member);
        memberSuspensionRepository.flush();

        // when
        boolean result = memberSuspensionRepository.existsCurrentSuspension(
                member.getId(),
                Instant.now()
        );

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 회원의 전역 밴은 false를 반환한다.")
    void excludeOtherMemberSuspensionFromExists() {
        // given
        Member member = saveMember();
        Member otherMember = saveMember();

        saveSuspension(otherMember, null);

        // when
        boolean result = memberSuspensionRepository.existsCurrentSuspension(
                member.getId(),
                Instant.now().plusSeconds(10)
        );

        // then
        assertThat(result).isFalse();
    }
}
