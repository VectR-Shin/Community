package io.github.vectr.community.member.service.profile;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.member.api.profile.ProfileCommandService;
import io.github.vectr.community.member.dto.profile.ProfileUpdateRequestDTO;
import io.github.vectr.community.member.dto.profile.ProfileUpdateResponseDTO;
import io.github.vectr.community.member.entity.Profile;
import io.github.vectr.community.member.exception.ProfileErrorCode;
import io.github.vectr.community.member.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileCommandServiceImpl implements ProfileCommandService {

    private final ProfileRepository profileRepository;

    // 현재 사용자의 프로필 정보 수정
    @Override
    @Transactional
    public ProfileUpdateResponseDTO updateProfile(Long memberId, ProfileUpdateRequestDTO request) {

        // 수정할 필드가 있는지 확인
        if (request.nickname() == null && request.isPublic() == null) {
            throw new BusinessException(
                    ProfileErrorCode.INVALID_PROFILE_UPDATE_REQUEST
            );
        }

        // 현재 사용자의 Profile 조회
        Profile profile = profileRepository.findByMember_Id(memberId)
                .orElseThrow(() -> new BusinessException(
                        ProfileErrorCode.PROFILE_NOT_FOUND
                ));

        // 닉네임 수정
        if (request.nickname() != null) {
            String nickname = request.nickname();

            // 닉네임 중복 확인
            // 닉네임 길이 조건 검증은 dto 에서 수행
            if (profileRepository.existsByNicknameAndMember_IdNot(nickname, memberId)) {
                throw new BusinessException(
                        ProfileErrorCode.DUPLICATE_NICKNAME
                );
            }

            profile.updateNickname(nickname);
        }

        // 프로필 공개 여부 수정
        if (request.isPublic() != null) {
            profile.updatePublic(request.isPublic());
        }

        // 수정 결과 반환
        return new ProfileUpdateResponseDTO(
                profile.getNickname(),
                profile.isPublicProfile()
        );
    }
}
