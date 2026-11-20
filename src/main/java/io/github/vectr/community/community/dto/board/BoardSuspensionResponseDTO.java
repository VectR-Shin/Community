package io.github.vectr.community.community.dto.board;

import io.github.vectr.community.community.enums.BoardSuspensionReason;

import java.time.Instant;

public record BoardSuspensionResponseDTO(
        Long suspensionId,
        Long boardId,
        BoardSuspensionReason reason,
        Instant startedAt,
        Instant endsAt
) {}
