package io.github.vectr.community.member.service.suspension;

import io.github.vectr.community.common.dto.CurrentSuspensionResponseDTO;
import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.member.dto.suspension.SuspensionResponseDTO;
import io.github.vectr.community.member.enums.MemberSuspensionReason;
import io.github.vectr.community.member.exception.MemberSuspensionErrorCode;
import io.github.vectr.community.member.repository.MemberSuspensionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SuspensionQueryServiceImplTest {

    @Mock
    private MemberSuspensionRepository suspensionRepository;

    @InjectMocks
    private SuspensionQueryServiceImpl suspensionQueryService;

    @Test
    @DisplayName("현재 적용 중인 전역 밴 정보를 조회한다.")
    void getCurrentSuspension() {
        // given
        Long memberId = 1L;

        SuspensionResponseDTO suspension = new SuspensionResponseDTO(
                1L,
                MemberSuspensionReason.OTHER,
                Instant.now().minusSeconds(3600),
                Instant.now().plusSeconds(3600)
        );

        when(suspensionRepository.findCurrentSuspension(
                eq(memberId),
                any(Instant.class)
        )).thenReturn(Optional.of(suspension));

        // when
        CurrentSuspensionResponseDTO<SuspensionResponseDTO> result =
                suspensionQueryService.getCurrentSuspension(memberId);

        // then
        assertThat(result.suspension()).isEqualTo(suspension);

        verify(suspensionRepository).findCurrentSuspension(
                eq(memberId),
                any(Instant.class)
        );
    }

    @Test
    @DisplayName("현재 적용 중인 전역 밴 정보가 없으면 null을 반환한다.")
    void getCurrentSuspensionWhenNotSuspended() {
        // given
        Long memberId = 1L;

        when(suspensionRepository.findCurrentSuspension(
                eq(memberId),
                any(Instant.class)
        )).thenReturn(Optional.empty());

        // when
        CurrentSuspensionResponseDTO<SuspensionResponseDTO> result =
                suspensionQueryService.getCurrentSuspension(memberId);

        // then
        assertThat(result.suspension()).isNull();

        verify(suspensionRepository).findCurrentSuspension(
                eq(memberId),
                any(Instant.class)
        );
    }

    @Test
    @DisplayName("현재 전역 밴이 적용되지 않은 사용자는 예외가 발생하지 않는다.")
    void validateNotBannedWhenNotSuspended() {
        // given
        Long memberId = 1L;

        when(suspensionRepository.existsCurrentSuspension(
                eq(memberId),
                any(Instant.class)
        )).thenReturn(false);

        // when & then
        assertThatCode(() -> suspensionQueryService.validateNotBanned(memberId))
                .doesNotThrowAnyException();

        verify(suspensionRepository).existsCurrentSuspension(
                eq(memberId),
                any(Instant.class)
        );
    }

    @Test
    @DisplayName("현재 전역 밴이 적용된 사용자는 MEMBER_BANNED 예외가 발생한다.")
    void validateNotBannedWhenSuspended() {
        // given
        Long memberId = 1L;

        when(suspensionRepository.existsCurrentSuspension(
                eq(memberId),
                any(Instant.class)
        )).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> suspensionQueryService.validateNotBanned(memberId))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(MemberSuspensionErrorCode.MEMBER_BANNED);

        verify(suspensionRepository).existsCurrentSuspension(
                eq(memberId),
                any(Instant.class)
        );
    }
}