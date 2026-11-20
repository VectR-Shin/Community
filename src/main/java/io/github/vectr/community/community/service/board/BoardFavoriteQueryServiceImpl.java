package io.github.vectr.community.community.service.board;

import io.github.vectr.community.common.dto.PageResponseDTO;
import io.github.vectr.community.community.api.board.BoardFavoriteQueryService;
import io.github.vectr.community.community.dto.board.BoardSummaryResponseDTO;
import io.github.vectr.community.community.repository.board.BoardFavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardFavoriteQueryServiceImpl implements BoardFavoriteQueryService {

    private final BoardFavoriteRepository boardFavoriteRepository;

    // 사용자가 즐겨찾기 한 게시판 목록을 최신순으로 조회한다.
    // 삭제된 게시판은 조회 대상이 아니다.
    @Override
    public PageResponseDTO<BoardSummaryResponseDTO> getFavoriteBoards(Long memberId, int page, int size) {
        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by(
                        Sort.Order.asc("createdAt"),
                        Sort.Order.asc("id")
                )
        );

        Page<BoardSummaryResponseDTO> favoriteBoards =
                boardFavoriteRepository.findFavoriteBoardsByMemberId(memberId, pageable);

        return new PageResponseDTO<> (
                favoriteBoards.getContent(),
                favoriteBoards.getNumber(),
                favoriteBoards.getSize(),
                favoriteBoards.getTotalElements(),
                favoriteBoards.getTotalPages()
        );
    }
}
