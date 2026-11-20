package io.github.vectr.community.community.service.board;

import io.github.vectr.community.community.api.board.BoardManagerCommandService;
import io.github.vectr.community.community.repository.board.BoardManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardManagerCommandServiceImpl implements BoardManagerCommandService {

    private final BoardManagerRepository boardManagerRepository;

    // 특정 사용자의 모든 게시판 관리자 정보 삭제
    @Override
    public void deleteAllBoardManagerByMemberId(Long memberId) {
        boardManagerRepository.deleteAllByMemberId(memberId);
    }
}
