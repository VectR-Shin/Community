package io.github.vectr.community.member.api.suspension;

import io.github.vectr.community.common.dto.CurrentSuspensionResponseDTO;
import io.github.vectr.community.member.dto.suspension.SuspensionResponseDTO;

public interface SuspensionQueryService {
    // 현재 사용자에게 적용중인 전역 밴 정보 조회
    CurrentSuspensionResponseDTO<SuspensionResponseDTO> getCurrentSuspension(Long memberId);

    // 현재 전역 밴 된 사용자라면 예외를 발생
    void validateNotBanned(Long memberId);
}
