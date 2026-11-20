package io.github.vectr.community.member.service.profile;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.member.api.profile.ProfileQueryService;
import io.github.vectr.community.member.dto.profile.ProfileResponseDTO;
import io.github.vectr.community.member.entity.Profile;
import io.github.vectr.community.member.exception.MemberErrorCode;
import io.github.vectr.community.member.exception.ProfileErrorCode;
import io.github.vectr.community.member.repository.MemberRepository;
import io.github.vectr.community.member.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileQueryServiceImpl implements ProfileQueryService {

    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;

    // 프로필 공개 설정한 사용자의 프로필 조회
    @Override
    public ProfileResponseDTO getProfile(Long memberId) {
        // 1. 사용자 존재/탈퇴 여부 확인
        if (!memberRepository.existsByIdAndDeletedAtIsNull(memberId)) {
            throw new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND);
        }

        // 2. 프로필 정보 조회
        Profile profile = profileRepository.findByMember_Id(memberId)
                .orElseThrow(() ->
                        new BusinessException(ProfileErrorCode.PROFILE_NOT_FOUND)
                );

        // 3. dto 변환 및 반환
        if (!profile.isPublicProfile()) {
            // 3.1. 프로필 비공개
            return new ProfileResponseDTO(
                    null,
                    profile.getNickname(),
                    false,
                    null
            );
        } else {
            // 3.2. 프로필 공개
            return new ProfileResponseDTO(
                    profile.getMember().getId(),
                    profile.getNickname(),
                    true,
                    profile.getCreatedAt()
            );
        }
    }
}
