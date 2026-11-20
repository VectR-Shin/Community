package io.github.vectr.community.member.service.user;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.member.api.user.UserQueryService;
import io.github.vectr.community.member.dto.user.AuthorInfoDTO;
import io.github.vectr.community.member.dto.user.UserResponseDTO;
import io.github.vectr.community.member.entity.Member;
import io.github.vectr.community.member.entity.Profile;
import io.github.vectr.community.member.exception.MemberErrorCode;
import io.github.vectr.community.member.exception.ProfileErrorCode;
import io.github.vectr.community.member.repository.MemberRepository;
import io.github.vectr.community.member.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;

    // 사용자 식별자를 기반으로 member, profile 정보 조회
    @Override
    public UserResponseDTO getUserInfo(Long memberId) {
        Member member = memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));

        Profile profile = profileRepository.findByMember_Id(memberId)
                .orElseThrow(() -> new BusinessException(ProfileErrorCode.PROFILE_NOT_FOUND));

        return new UserResponseDTO(
                member.getId(),
                member.getRole(),
                profile.getNickname(),
                member.getEmail(),
                profile.isPublicProfile(),
                member.getCreatedAt()
        );
    }

    // 사용자 식별자를 기반으로 리소스 작성자 정보 단건 조회
    @Override
    public AuthorInfoDTO getAuthorInfo(Long memberId) {
        return memberRepository.findAuthorInfoById(memberId)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    // 사용자 식별자를 기반으로 리소스 작성자 정보 벌크 조회
    @Override
    public List<AuthorInfoDTO> getAuthorInfoList(Collection<Long> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            return List.of();
        }

        // 중복 ID 제거
        Set<Long> distinctMemberIds = new HashSet<>(memberIds);

        // IN 조건 사용
        return memberRepository.findAuthorInfosByIds(distinctMemberIds);
    }
}
