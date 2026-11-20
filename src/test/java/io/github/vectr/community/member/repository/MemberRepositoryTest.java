
package io.github.vectr.community.member.repository;

import io.github.vectr.community.common.config.JpaAuditingTestConfig;
import io.github.vectr.community.member.dto.user.AuthorInfoDTO;
import io.github.vectr.community.member.entity.Member;
import io.github.vectr.community.member.entity.Profile;
import io.github.vectr.community.member.enums.MemberRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import(JpaAuditingTestConfig.class)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    @DisplayName("Keycloak Sub 로 회원을 조회한다")
    void findByKeycloakSub() {
        // given
        Member member = saveMember("keycloak-sub-1", "test1@example.com");

        // when
        Optional<Member> result =
                memberRepository.findByKeycloakSub("keycloak-sub-1");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(member.getId());
        assertThat(result.get().getEmail()).isEqualTo("test1@example.com");
    }

    @Test
    @DisplayName("존재하지 않는 Keycloak Sub로 조회하면 빈 Optional을 반환한다")
    void findByKeycloakSub_notFound() {
        // when
        Optional<Member> result =
                memberRepository.findByKeycloakSub("unknown-sub");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("탈퇴하지 않은 회원을 ID로 조회한다")
    void findByIdAndDeletedAtIsNull() {
        // given
        Member member = saveMember("keycloak-sub-1", "test1@example.com");

        // when
        Optional<Member> result =
                memberRepository.findByIdAndDeletedAtIsNull(member.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("탈퇴한 회원은 ID로 조회되지 않는다")
    void findByIdAndDeletedAtIsNull_deletedMember() {
        // given
        Member member = saveMember("keycloak-sub-1", "test1@example.com");

        ReflectionTestUtils.setField(member, "deletedAt", Instant.now());
        memberRepository.flush();

        // when
        Optional<Member> result =
                memberRepository.findByIdAndDeletedAtIsNull(member.getId());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Keycloak Sub로 회원 존재 여부를 확인한다")
    void existsByKeycloakSub() {
        // given
        saveMember("keycloak-sub-1", "test1@example.com");

        // when
        boolean result =
                memberRepository.existsByKeycloakSub("keycloak-sub-1");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 Keycloak Sub이면 false를 반환한다")
    void existsByKeycloakSub_notFound() {
        // when
        boolean result =
                memberRepository.existsByKeycloakSub("unknown-sub");

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("회원 ID로 작성자 정보를 단건 조회한다")
    void findAuthorInfoById() {
        // given
        Member member = saveMember("keycloak-sub-1", "test1@example.com");
        saveProfile(member, "테스트닉네임");

        // when
        Optional<AuthorInfoDTO> result =
                memberRepository.findAuthorInfoById(member.getId());

        // then
        assertThat(result).isPresent();

        AuthorInfoDTO authorInfo = result.get();

        assertThat(authorInfo.memberId()).isEqualTo(member.getId());
        assertThat(authorInfo.nickname()).isEqualTo("테스트닉네임");
        assertThat(authorInfo.withdrawn()).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 회원 ID로 작성자 정보를 조회하면 빈 Optional을 반환한다")
    void findAuthorInfoById_notFound() {
        // when
        Optional<AuthorInfoDTO> result =
                memberRepository.findAuthorInfoById(999L);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("프로필이 없는 회원도 작성자 정보 조회가 가능하다")
    void findAuthorInfoById_withoutProfile() {
        // given
        Member member = saveMember("keycloak-sub-1", "test1@example.com");

        // when
        Optional<AuthorInfoDTO> result =
                memberRepository.findAuthorInfoById(member.getId());

        // then
        assertThat(result).isPresent();

        AuthorInfoDTO authorInfo = result.get();

        assertThat(authorInfo.memberId()).isEqualTo(member.getId());
        assertThat(authorInfo.nickname()).isNull();
        assertThat(authorInfo.withdrawn()).isFalse();
    }

    @Test
    @DisplayName("탈퇴한 회원의 작성자 정보는 withdrawn이 true이다")
    void findAuthorInfoById_withdrawnMember() {
        // given
        Member member = saveMember("keycloak-sub-1", "test1@example.com");
        saveProfile(member, "테스트닉네임");

        ReflectionTestUtils.setField(member, "deletedAt", Instant.now());
        memberRepository.flush();

        // when
        Optional<AuthorInfoDTO> result =
                memberRepository.findAuthorInfoById(member.getId());

        // then
        assertThat(result).isPresent();

        AuthorInfoDTO authorInfo = result.get();

        assertThat(authorInfo.memberId()).isEqualTo(member.getId());
        assertThat(authorInfo.nickname()).isEqualTo("테스트닉네임");
        assertThat(authorInfo.withdrawn()).isTrue();
    }

    @Test
    @DisplayName("회원 ID 목록으로 작성자 정보를 벌크 조회한다")
    void findAuthorInfosByIds() {
        // given
        Member member1 = saveMember("keycloak-sub-1", "test1@example.com");
        Member member2 = saveMember("keycloak-sub-2", "test2@example.com");
        Member member3 = saveMember("keycloak-sub-3", "test3@example.com");

        saveProfile(member1, "닉네임1");
        saveProfile(member2, "닉네임2");
        saveProfile(member3, "닉네임3");

        List<Long> memberIds = List.of(
                member1.getId(),
                member2.getId(),
                member3.getId()
        );

        // when
        List<AuthorInfoDTO> result =
                memberRepository.findAuthorInfosByIds(memberIds);

        // then
        assertThat(result).hasSize(3);

        assertThat(result)
                .extracting(AuthorInfoDTO::memberId)
                .containsExactlyInAnyOrderElementsOf(memberIds);

        assertThat(result)
                .extracting(AuthorInfoDTO::nickname)
                .containsExactlyInAnyOrder("닉네임1", "닉네임2", "닉네임3");

        assertThat(result)
                .extracting(AuthorInfoDTO::withdrawn)
                .containsOnly(false);
    }

    @Test
    @DisplayName("일부 회원 ID만 전달하면 해당 회원의 작성자 정보만 조회한다")
    void findAuthorInfosByIds_partialIds() {
        // given
        Member member1 = saveMember("keycloak-sub-1", "test1@example.com");
        Member member2 = saveMember("keycloak-sub-2", "test2@example.com");
        Member member3 = saveMember("keycloak-sub-3", "test3@example.com");

        saveProfile(member1, "닉네임1");
        saveProfile(member2, "닉네임2");
        saveProfile(member3, "닉네임3");

        List<Long> memberIds = List.of(
                member1.getId(),
                member3.getId()
        );

        // when
        List<AuthorInfoDTO> result =
                memberRepository.findAuthorInfosByIds(memberIds);

        // then
        assertThat(result).hasSize(2);

        assertThat(result)
                .extracting(AuthorInfoDTO::memberId)
                .containsExactlyInAnyOrder(
                        member1.getId(),
                        member3.getId()
                );
    }

    @Test
    @DisplayName("프로필이 없는 회원도 벌크 조회 결과에 포함된다")
    void findAuthorInfosByIds_withoutProfile() {
        // given
        Member member1 = saveMember("keycloak-sub-1", "test1@example.com");
        Member member2 = saveMember("keycloak-sub-2", "test2@example.com");

        saveProfile(member1, "닉네임1");

        List<Long> memberIds = List.of(
                member1.getId(),
                member2.getId()
        );

        // when
        List<AuthorInfoDTO> result =
                memberRepository.findAuthorInfosByIds(memberIds);

        // then
        assertThat(result).hasSize(2);

        AuthorInfoDTO memberWithoutProfile = result.stream()
                .filter(author -> author.memberId().equals(member2.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(memberWithoutProfile.nickname()).isNull();
        assertThat(memberWithoutProfile.withdrawn()).isFalse();
    }

    private Member saveMember(String keycloakSub, String email) {
        return memberRepository.save(
                new Member(keycloakSub, email, MemberRole.USER)
        );
    }

    private Profile saveProfile(Member member, String nickname) {
        return profileRepository.save(
                new Profile(member, nickname)
        );
    }
}
