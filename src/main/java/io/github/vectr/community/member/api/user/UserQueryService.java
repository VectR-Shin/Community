package io.github.vectr.community.member.api.user;

import io.github.vectr.community.member.dto.user.AuthorInfoDTO;
import io.github.vectr.community.member.dto.user.UserResponseDTO;

import java.util.Collection;
import java.util.List;

public interface UserQueryService {
    // 사용자 식별자를 기반으로 member, profile 정보 단건 조회
    UserResponseDTO getUserInfo(Long memberId);

    // 사용자 식별자를 기반으로 리소스 작성자 정보 단건 조회
    AuthorInfoDTO getAuthorInfo(Long memberId);

    // 사용자 식별자를 기반으로 리소스 작성자 정보 벌크 조회
    List<AuthorInfoDTO> getAuthorInfoList(Collection<Long> memberIds);
}
