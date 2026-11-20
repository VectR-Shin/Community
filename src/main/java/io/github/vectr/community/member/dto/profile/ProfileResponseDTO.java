package io.github.vectr.community.member.dto.profile;

import java.time.Instant;

public record ProfileResponseDTO(
        Long userId,
        String nickname,
        Boolean isPublic,
        Instant createdAt
) {}
