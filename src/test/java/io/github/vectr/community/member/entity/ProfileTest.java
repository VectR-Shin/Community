package io.github.vectr.community.member.entity;

import io.github.vectr.community.member.enums.MemberProvider;
import io.github.vectr.community.member.enums.MemberRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProfileTest {

    private Member createDefaultMember() {
        return new Member(
                "keycloak-sub",
                "test@example.com",
                MemberRole.USER
        );
    }

    @Test
    @DisplayName("Profile 객체를 정상적으로 생성한다.")
    void createProfile() {
        // given
        Member member = createDefaultMember();
        String nickname = "테스트닉네임";

        // when
        Profile profile = new Profile(member, nickname);

        // then
        assertThat(profile.getMember()).isEqualTo(member);
        assertThat(profile.getNickname()).isEqualTo(nickname);
        assertThat(profile.isPublicProfile()).isTrue();
    }

    @Test
    @DisplayName("Member가 null이면 예외가 발생한다.")
    void createProfileWithNullMember() {
        assertThatThrownBy(() ->
                new Profile(null, "테스트닉네임")
        ).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Nickname이 null이면 예외가 발생한다.")
    void createProfileWithNullNickname() {
        assertThatThrownBy(() ->
                new Profile(createDefaultMember(), null)
        ).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("닉네임을 정상적으로 변경한다.")
    void updateNickname() {
        // given
        Profile profile = new Profile(
                createDefaultMember(),
                "기존닉네임"
        );
        String newNickname = "새로운닉네임";

        // when
        profile.updateNickname(newNickname);

        // then
        assertThat(profile.getNickname()).isEqualTo(newNickname);
    }

    @Test
    @DisplayName("변경할 닉네임이 null이면 예외가 발생한다.")
    void updateNicknameWithNull() {
        // given
        Profile profile = new Profile(
                createDefaultMember(),
                "기존닉네임"
        );

        // when & then
        assertThatThrownBy(() -> profile.updateNickname(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("프로필 공개 여부를 비공개로 변경한다.")
    void updatePublicToFalse() {
        // given
        Profile profile = new Profile(
                createDefaultMember(),
                "테스트닉네임"
        );

        // when
        profile.updatePublic(false);

        // then
        assertThat(profile.isPublicProfile()).isFalse();
    }

    @Test
    @DisplayName("프로필 공개 여부를 공개로 변경한다.")
    void updatePublicToTrue() {
        // given
        Profile profile = new Profile(
                createDefaultMember(),
                "테스트닉네임"
        );
        profile.updatePublic(false);

        // when
        profile.updatePublic(true);

        // then
        assertThat(profile.isPublicProfile()).isTrue();
    }
}