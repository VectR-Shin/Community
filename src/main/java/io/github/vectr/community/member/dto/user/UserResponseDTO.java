package io.github.vectr.community.member.dto.user;

import io.github.vectr.community.member.enums.MemberRole;

import java.time.Instant;

public record UserResponseDTO(
        Long memberId,
        MemberRole role,
        String nickname,
        String email,
        Boolean isPublic,
        Instant createdAt
) {
}
