package io.github.vectr.community.member.repository;

import io.github.vectr.community.common.config.JpaAuditingTestConfig;
import io.github.vectr.community.member.entity.Member;
import io.github.vectr.community.member.entity.Profile;
import io.github.vectr.community.member.enums.MemberRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import(JpaAuditingTestConfig.class)
class ProfileRepositoryTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("Member ID로 프로필을 조회한다.")
    void findByMemberId_success() {
        // given
        Member member = createMember("test-sub-1", "test1@example.com");
        memberRepository.save(member);

        Profile profile = createProfile(member, "테스트회원");
        profileRepository.save(profile);

        // when
        Optional<Profile> result =
                profileRepository.findByMember_Id(member.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getNickname()).isEqualTo("테스트회원");
    }

    @Test
    @DisplayName("프로필이 존재하지 않으면 빈 Optional을 반환한다.")
    void findByMemberId_notFound() {
        // given
        Long memberId = 999L;

        // when
        Optional<Profile> result =
                profileRepository.findByMember_Id(memberId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("닉네임이 존재하면 true를 반환한다.")
    void existsByNickname_true() {
        // given
        Member member = createMember("test-sub-2", "test2@example.com");
        memberRepository.save(member);

        Profile profile = createProfile(member, "중복닉네임");
        profileRepository.save(profile);

        // when
        boolean result = profileRepository.existsByNickname("중복닉네임");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("닉네임이 존재하지 않으면 false를 반환한다.")
    void existsByNickname_false() {
        // when
        boolean result = profileRepository.existsByNickname("없는닉네임");

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("다른 회원이 동일한 닉네임을 사용 중이면 true를 반환한다.")
    void existsByNicknameAndMemberIdNot_true() {
        // given
        Member member1 = createMember("test-sub-3", "test3@example.com");
        Member member2 = createMember("test-sub-4", "test4@example.com");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Profile profile = createProfile(member1, "중복닉네임");
        profileRepository.save(profile);

        // when
        boolean result = profileRepository.existsByNicknameAndMember_IdNot(
                "중복닉네임",
                member2.getId()
        );

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("자신의 현재 닉네임은 중복으로 판단하지 않는다.")
    void existsByNicknameAndMemberIdNot_false_ownNickname() {
        // given
        Member member = createMember("test-sub-5", "test5@example.com");
        memberRepository.save(member);

        Profile profile = createProfile(member, "현재닉네임");
        profileRepository.save(profile);

        // when
        boolean result = profileRepository.existsByNicknameAndMember_IdNot(
                "현재닉네임",
                member.getId()
        );

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("동일한 닉네임을 사용하는 회원이 없으면 false를 반환한다.")
    void existsByNicknameAndMemberIdNot_false_notFound() {
        // given
        Member member = createMember("test-sub-6", "test6@example.com");
        memberRepository.save(member);

        // when
        boolean result = profileRepository.existsByNicknameAndMember_IdNot(
                "없는닉네임",
                member.getId()
        );

        // then
        assertThat(result).isFalse();
    }

    private Member createMember(String keycloakSub, String email) {
        // 실제 Member 생성자에 맞게 수정
        return new Member(keycloakSub, email, MemberRole.USER);
    }

    private Profile createProfile(Member member, String nickname) {
        // 실제 Profile 생성자에 맞게 수정
        return new Profile(member, nickname);
    }
}