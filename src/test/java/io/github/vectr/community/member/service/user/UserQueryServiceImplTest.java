package io.github.vectr.community.member.service.user;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.member.dto.user.AuthorInfoDTO;
import io.github.vectr.community.member.dto.user.UserResponseDTO;
import io.github.vectr.community.member.entity.Member;
import io.github.vectr.community.member.entity.Profile;
import io.github.vectr.community.member.enums.MemberRole;
import io.github.vectr.community.member.exception.MemberErrorCode;
import io.github.vectr.community.member.exception.ProfileErrorCode;
import io.github.vectr.community.member.repository.MemberRepository;
import io.github.vectr.community.member.repository.ProfileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private UserQueryServiceImpl userQueryService;

    private static final Long MEMBER_ID = 1L;
    private static final String KEYCLOAK_SUB = "keycloak-sub-123";
    private static final String EMAIL = "test@example.com";
    private static final String NICKNAME = "testuser";

    @Nested
    @DisplayName("사용자 정보 조회")
    class GetUserInfo {

        @Test
        @DisplayName("회원 및 프로필 정보를 정상적으로 조회한다.")
        void getUserInfo() {
            // given
            Member member = new Member(
                    KEYCLOAK_SUB,
                    EMAIL,
                    MemberRole.USER
            );

            ReflectionTestUtils.setField(member, "id", MEMBER_ID);

            Profile profile = mock(Profile.class);

            when(memberRepository.findByIdAndDeletedAtIsNull(MEMBER_ID))
                    .thenReturn(Optional.of(member));

            when(profileRepository.findByMember_Id(MEMBER_ID))
                    .thenReturn(Optional.of(profile));

            when(profile.getNickname()).thenReturn(NICKNAME);
            when(profile.isPublicProfile()).thenReturn(true);

            // when
            UserResponseDTO result = userQueryService.getUserInfo(MEMBER_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result.memberId()).isEqualTo(MEMBER_ID);
            assertThat(result.role()).isEqualTo(MemberRole.USER);
            assertThat(result.nickname()).isEqualTo(NICKNAME);
            assertThat(result.email()).isEqualTo(EMAIL);
            assertThat(result.isPublic()).isTrue();
            assertThat(result.createdAt()).isEqualTo(member.getCreatedAt());

            verify(memberRepository).findByIdAndDeletedAtIsNull(MEMBER_ID);
            verify(profileRepository).findByMember_Id(MEMBER_ID);
        }

        @Test
        @DisplayName("회원이 존재하지 않으면 MEMBER_NOT_FOUND 예외가 발생한다.")
        void getUserInfoWithNonexistentMember() {
            // given
            when(memberRepository.findByIdAndDeletedAtIsNull(MEMBER_ID))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userQueryService.getUserInfo(MEMBER_ID))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue(
                            "errorCode",
                            MemberErrorCode.MEMBER_NOT_FOUND
                    );

            verifyNoInteractions(profileRepository);
        }

        @Test
        @DisplayName("프로필이 존재하지 않으면 PROFILE_NOT_FOUND 예외가 발생한다.")
        void getUserInfoWithNonexistentProfile() {
            // given
            Member member = new Member(
                    KEYCLOAK_SUB,
                    EMAIL,
                    MemberRole.USER
            );

            when(memberRepository.findByIdAndDeletedAtIsNull(MEMBER_ID))
                    .thenReturn(Optional.of(member));

            when(profileRepository.findByMember_Id(MEMBER_ID))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userQueryService.getUserInfo(MEMBER_ID))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue(
                            "errorCode",
                            ProfileErrorCode.PROFILE_NOT_FOUND
                    );
        }
    }

    @Nested
    @DisplayName("작성자 정보 단건 조회")
    class GetAuthorInfo {

        @Test
        @DisplayName("작성자 정보를 정상적으로 조회한다.")
        void getAuthorInfo() {
            // given
            AuthorInfoDTO authorInfo = mock(AuthorInfoDTO.class);

            when(memberRepository.findAuthorInfoById(MEMBER_ID))
                    .thenReturn(Optional.of(authorInfo));

            // when
            AuthorInfoDTO result = userQueryService.getAuthorInfo(MEMBER_ID);

            // then
            assertThat(result).isSameAs(authorInfo);

            verify(memberRepository).findAuthorInfoById(MEMBER_ID);
        }

        @Test
        @DisplayName("작성자가 존재하지 않으면 MEMBER_NOT_FOUND 예외가 발생한다.")
        void getAuthorInfoWithNonexistentMember() {
            // given
            when(memberRepository.findAuthorInfoById(MEMBER_ID))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userQueryService.getAuthorInfo(MEMBER_ID))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue(
                            "errorCode",
                            MemberErrorCode.MEMBER_NOT_FOUND
                    );
        }
    }

    @Nested
    @DisplayName("작성자 정보 벌크 조회")
    class GetAuthorInfoList {

        @Test
        @DisplayName("여러 작성자 정보를 정상적으로 조회한다.")
        void getAuthorInfoList() {
            // given
            List<Long> memberIds = List.of(1L, 2L, 3L);

            List<AuthorInfoDTO> authorInfos = List.of(
                    mock(AuthorInfoDTO.class),
                    mock(AuthorInfoDTO.class)
            );

            when(memberRepository.findAuthorInfosByIds(anySet()))
                    .thenReturn(authorInfos);

            // when
            List<AuthorInfoDTO> result =
                    userQueryService.getAuthorInfoList(memberIds);

            // then
            assertThat(result).isEqualTo(authorInfos);

            verify(memberRepository).findAuthorInfosByIds(
                    Set.of(1L, 2L, 3L)
            );
        }

        @Test
        @DisplayName("중복된 회원 ID는 제거한 후 조회한다.")
        void getAuthorInfoListWithDuplicateIds() {
            // given
            List<Long> memberIds = List.of(1L, 2L, 1L, 3L, 2L);

            List<AuthorInfoDTO> authorInfos = List.of(
                    mock(AuthorInfoDTO.class),
                    mock(AuthorInfoDTO.class)
            );

            when(memberRepository.findAuthorInfosByIds(anySet()))
                    .thenReturn(authorInfos);

            // when
            List<AuthorInfoDTO> result =
                    userQueryService.getAuthorInfoList(memberIds);

            // then
            assertThat(result).isEqualTo(authorInfos);

            verify(memberRepository).findAuthorInfosByIds(
                    Set.of(1L, 2L, 3L)
            );
        }

        @Test
        @DisplayName("회원 ID 목록이 null 이면 빈 목록을 반환한다.")
        void getAuthorInfoListWithNull() {
            // when
            List<AuthorInfoDTO> result =
                    userQueryService.getAuthorInfoList(null);

            // then
            assertThat(result).isEmpty();

            verifyNoInteractions(memberRepository);
        }

        @Test
        @DisplayName("회원 ID 목록이 비어 있으면 빈 목록을 반환한다.")
        void getAuthorInfoListWithEmptyList() {
            // when
            List<AuthorInfoDTO> result =
                    userQueryService.getAuthorInfoList(List.of());

            // then
            assertThat(result).isEmpty();

            verifyNoInteractions(memberRepository);
        }
    }
}
