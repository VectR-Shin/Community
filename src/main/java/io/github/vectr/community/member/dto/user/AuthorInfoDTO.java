package io.github.vectr.community.member.dto.user;

public record AuthorInfoDTO(
        Long memberId,
        String nickname,
        boolean withdrawn
) {}
