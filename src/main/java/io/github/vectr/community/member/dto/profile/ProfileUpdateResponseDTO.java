package io.github.vectr.community.member.dto.profile;

public record ProfileUpdateResponseDTO(
        String nickname,
        boolean isPublic
) {}