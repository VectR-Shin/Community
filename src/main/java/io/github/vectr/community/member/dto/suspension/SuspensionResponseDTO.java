package io.github.vectr.community.member.dto.suspension;

import io.github.vectr.community.member.enums.MemberSuspensionReason;

import java.time.Instant;

public record SuspensionResponseDTO (
        Long suspensionId,
        MemberSuspensionReason reason,
        Instant startedAt,
        Instant endsAt
){}
