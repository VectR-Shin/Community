package io.github.vectr.community.member.api.profile;

import io.github.vectr.community.member.dto.profile.ProfileResponseDTO;

public interface ProfileQueryService {

    // 프로필 공개 설정한 사용자의 프로필 조회
    ProfileResponseDTO getProfile(Long memberId);
}
