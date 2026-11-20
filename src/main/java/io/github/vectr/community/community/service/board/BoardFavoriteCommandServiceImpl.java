package io.github.vectr.community.community.service.board;

import io.github.vectr.community.community.api.board.BoardFavoriteCommandService;
import io.github.vectr.community.community.repository.board.BoardFavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardFavoriteCommandServiceImpl implements BoardFavoriteCommandService {

    private final BoardFavoriteRepository boardFavoriteRepository;

    // 특정 사용자의 모든 게시판 즐겨찾기 정보 삭제
    @Override
    public void deleteAllBoardFavoriteByMemberId(Long memberId) {
        boardFavoriteRepository.deleteAllByMemberId(memberId);
    }
}
