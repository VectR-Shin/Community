package io.github.vectr.community.member.api.profile;

import io.github.vectr.community.member.dto.profile.ProfileUpdateRequestDTO;
import io.github.vectr.community.member.dto.profile.ProfileUpdateResponseDTO;
import jakarta.validation.Valid;

public interface ProfileCommandService {
    // 현재 사용자의 프로필 정보 수정
    ProfileUpdateResponseDTO updateProfile(Long memberId, @Valid ProfileUpdateRequestDTO request);
}
