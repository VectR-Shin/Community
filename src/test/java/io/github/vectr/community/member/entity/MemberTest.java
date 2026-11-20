
package io.github.vectr.community.member.entity;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.member.enums.MemberRole;
import io.github.vectr.community.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    private static final String KEYCLOAK_SUB = "keycloak-sub-123";
    private static final String EMAIL = "test@example.com";

    @Nested
    @DisplayName("회원 생성")
    class CreateMember {

        @Test
        @DisplayName("회원 생성 시 전달한 값이 초기화된다.")
        void createMember() {
            // given & when
            Member member = new Member(
                    KEYCLOAK_SUB,
                    EMAIL,
                    MemberRole.USER
            );

            // then
            assertThat(member.getKeycloakSub()).isEqualTo(KEYCLOAK_SUB);
            assertThat(member.getEmail()).isEqualTo(EMAIL);
            assertThat(member.getRole()).isEqualTo(MemberRole.USER);
            assertThat(member.getDeletedAt()).isNull();
            assertThat(member.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("Keycloak sub가 null이면 예외가 발생한다.")
        void createMemberWithNullKeycloakSub() {
            assertThatThrownBy(() ->
                    new Member(null, EMAIL, MemberRole.USER)
            ).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("이메일이 null이면 예외가 발생한다.")
        void createMemberWithNullEmail() {
            assertThatThrownBy(() ->
                    new Member(KEYCLOAK_SUB, null, MemberRole.USER)
            ).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("회원 권한이 null이면 예외가 발생한다.")
        void createMemberWithNullRole() {
            assertThatThrownBy(() ->
                    new Member(KEYCLOAK_SUB, EMAIL, null)
            ).isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("회원 탈퇴")
    class WithdrawMember {

        @Test
        @DisplayName("회원 탈퇴 시 탈퇴 일시가 기록되고 계정 정보가 초기화된다.")
        void withdrawMember() {
            // given
            Member member = new Member(
                    KEYCLOAK_SUB,
                    EMAIL,
                    MemberRole.USER
            );

            // when
            member.withdraw();

            // then
            assertThat(member.getDeletedAt()).isNotNull();
            assertThat(member.getDeletedAt()).isBeforeOrEqualTo(Instant.now());

            assertThat(member.getKeycloakSub()).isNull();
            assertThat(member.getEmail()).isNull();
            assertThat(member.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("이미 탈퇴한 회원은 다시 탈퇴할 수 없다.")
        void withdrawAlreadyWithdrawnMember() {
            // given
            Member member = new Member(
                    KEYCLOAK_SUB,
                    EMAIL,
                    MemberRole.USER
            );

            member.withdraw();

            // when & then
            assertThatThrownBy(member::withdraw)
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue(
                            "errorCode",
                            MemberErrorCode.MEMBER_ALREADY_WITHDRAWN
                    );
        }
    }

    @Nested
    @DisplayName("회원 상태")
    class MemberStatus {

        @Test
        @DisplayName("탈퇴하지 않은 회원은 삭제 상태가 아니다.")
        void activeMemberIsNotDeleted() {
            // given
            Member member = new Member(
                    KEYCLOAK_SUB,
                    EMAIL,
                    MemberRole.USER
            );

            // when & then
            assertThat(member.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("탈퇴한 회원은 삭제 상태이다.")
        void withdrawnMemberIsDeleted() {
            // given
            Member member = new Member(
                    KEYCLOAK_SUB,
                    EMAIL,
                    MemberRole.USER
            );

            // when
            member.withdraw();

            // then
            assertThat(member.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("회원 권한 변경")
    class UpdateMemberRole {

        @Test
        @DisplayName("회원 권한을 변경할 수 있다.")
        void updateRole() {
            // given
            Member member = new Member(
                    KEYCLOAK_SUB,
                    EMAIL,
                    MemberRole.USER
            );

            // when
            member.updateRole(MemberRole.ADMIN);

            // then
            assertThat(member.getRole()).isEqualTo(MemberRole.ADMIN);
        }

        @Test
        @DisplayName("변경할 권한이 null 이면 예외가 발생한다.")
        void updateRoleWithNull() {
            // given
            Member member = new Member(
                    KEYCLOAK_SUB,
                    EMAIL,
                    MemberRole.USER
            );

            // when & then
            assertThatThrownBy(() -> member.updateRole(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}
