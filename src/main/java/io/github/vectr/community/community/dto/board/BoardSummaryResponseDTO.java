package io.github.vectr.community.community.dto.board;

public record BoardSummaryResponseDTO(
        long boardId,
        String boardName,
        long categoryId,
        String categoryName
) {}
