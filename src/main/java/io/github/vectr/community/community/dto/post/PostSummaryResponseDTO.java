package io.github.vectr.community.community.dto.post;

import java.time.Instant;

public record PostSummaryResponseDTO(
        long postId,
        long boardId,
        String boardName,
        long authorId,
        String authorNickname,
        String title,
        long viewCount,
        long commentCount,
        long likeCount,
        long dislikeCount,
        boolean isPopular,
        Instant createdAt
) {}
