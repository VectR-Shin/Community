package io.github.vectr.community.member.service.suspension;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.member.api.suspension.SuspensionQueryService;
import io.github.vectr.community.common.dto.CurrentSuspensionResponseDTO;
import io.github.vectr.community.member.dto.suspension.SuspensionResponseDTO;
import io.github.vectr.community.member.exception.MemberSuspensionErrorCode;
import io.github.vectr.community.member.repository.MemberSuspensionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SuspensionQueryServiceImpl implements SuspensionQueryService {

    private final MemberSuspensionRepository suspensionRepository;

    // 현재 사용자에게 적용중인 전역 밴 정보 조회
    @Override
    public CurrentSuspensionResponseDTO<SuspensionResponseDTO> getCurrentSuspension(Long memberId) {
        SuspensionResponseDTO suspensionResponseDTO
                = suspensionRepository.findCurrentSuspension(memberId, Instant.now())
                .orElse(null);

        return new CurrentSuspensionResponseDTO<>(suspensionResponseDTO);
    }

    // 현재 전역 밴 된 사용자라면 예외를 발생
    @Override
    public void validateNotBanned(Long memberId) {
        if (suspensionRepository.existsCurrentSuspension(memberId, Instant.now())) {
            throw new BusinessException(MemberSuspensionErrorCode.MEMBER_BANNED);
        }
    }
}
