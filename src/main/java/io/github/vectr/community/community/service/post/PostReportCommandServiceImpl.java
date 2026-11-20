package io.github.vectr.community.community.service.post;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.community.api.post.PostReportCommandService;
import io.github.vectr.community.community.dto.report.ReportRequestDTO;
import io.github.vectr.community.community.entity.board.Board;
import io.github.vectr.community.community.entity.post.Post;
import io.github.vectr.community.community.entity.post.PostReport;
import io.github.vectr.community.community.enums.PostType;
import io.github.vectr.community.community.exception.BoardErrorCode;
import io.github.vectr.community.community.exception.BoardSuspensionErrorCode;
import io.github.vectr.community.community.exception.PostErrorCode;
import io.github.vectr.community.community.exception.ReportErrorCode;
import io.github.vectr.community.community.repository.board.BoardMemberSuspensionRepository;
import io.github.vectr.community.community.repository.board.BoardRepository;
import io.github.vectr.community.community.repository.post.PostReportRepository;
import io.github.vectr.community.community.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class PostReportCommandServiceImpl implements PostReportCommandService {

    private final PostRepository postRepository;
    private final BoardMemberSuspensionRepository boardMemberSuspensionRepository;
    private final PostReportRepository postReportRepository;
    private final BoardRepository boardRepository;

    // 게시글 신고 생성
    @Override
    public void createPostReport(Long memberId, ReportRequestDTO requestDTO) {
        // 1. 신고 대상의 존재/삭제 여부 판단
        Post post = postRepository.findByIdAndPostTypeAndDeletedAtIsNull(requestDTO.targetId(), PostType.POST)
                .orElseThrow(() ->
                        new BusinessException(PostErrorCode.POST_NOT_FOUND));

        // 2. 게시글이 속한 게시판의 존재/삭제 여부 판단
        Long boardId = post.getBoard().getId();

        if (!boardRepository.existsByIdAndDeletedAtIsNull(boardId)) {
            throw new BusinessException(BoardErrorCode.BOARD_NOT_FOUND);
        }

        // 3. 사용자가 게시글이 속한 게시판에서 밴을 당했는지 판단
        if (boardMemberSuspensionRepository.existsCurrentBoardSuspension(memberId, boardId, Instant.now())) {
            throw new BusinessException(BoardSuspensionErrorCode.BOARD_MEMBER_BANNED);
        }

        // 4. 신고 게시글의 작성자가 본인인지 확인
        if (Objects.equals(memberId, post.getMemberId())) {
            throw new BusinessException(ReportErrorCode.CANNOT_REPORT_OWN_RESOURCE);
        }

        // 5. 신고 생성 및 저장
        PostReport report = new PostReport(
                post,
                memberId,
                requestDTO.content(),
                requestDTO.reason()
        );

        postReportRepository.save(report);
    }
}