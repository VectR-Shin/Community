package io.github.vectr.community.community.service.comment;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.community.api.comment.CommentReportCommandService;
import io.github.vectr.community.community.dto.report.ReportRequestDTO;
import io.github.vectr.community.community.entity.comment.Comment;
import io.github.vectr.community.community.entity.comment.CommentReport;
import io.github.vectr.community.community.entity.post.Post;
import io.github.vectr.community.community.enums.PostType;
import io.github.vectr.community.community.exception.*;
import io.github.vectr.community.community.repository.board.BoardMemberSuspensionRepository;
import io.github.vectr.community.community.repository.board.BoardRepository;
import io.github.vectr.community.community.repository.comment.CommentReportRepository;
import io.github.vectr.community.community.repository.comment.CommentRepository;
import io.github.vectr.community.community.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentReportCommandServiceImpl implements CommentReportCommandService {

    private final BoardRepository boardRepository;
    private final BoardMemberSuspensionRepository boardMemberSuspensionRepository;
    private final CommentRepository commentRepository;
    private final CommentReportRepository commentReportRepository;
    private final PostRepository postRepository;

    // 댓글 신고 생성
    @Override
    public void createCommentReport(Long memberId, ReportRequestDTO requestDTO) {
        // 1. 신고 대상의 존재/삭제 여부 판단
        Comment comment = commentRepository.findByIdAndDeletedAtIsNull(requestDTO.targetId())
                .orElseThrow(() ->
                        new BusinessException(CommentErrorCode.COMMENT_NOT_FOUND)
                );

        // 1. 댓글이 속한 게시글의 존재/삭제 여부 판단
        Long postId = comment.getPost().getId();

        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() ->
                        new BusinessException(PostErrorCode.POST_NOT_FOUND)
                );

        // 2. 댓글이 속한 게시판의 존재/삭제 여부 판단
        Long boardId = post.getBoard().getId();

        if (!boardRepository.existsByIdAndDeletedAtIsNull(boardId)) {
            throw new BusinessException(BoardErrorCode.BOARD_NOT_FOUND);
        }

        // 3. 사용자가 댓글이 속한 게시판에서 밴을 당했는지 판단
        if (boardMemberSuspensionRepository.existsCurrentBoardSuspension(memberId, boardId, Instant.now())) {
            throw new BusinessException(BoardSuspensionErrorCode.BOARD_MEMBER_BANNED);
        }

        // 4. 신고 댓글의 작성자가 본인인지 확인
        if (Objects.equals(memberId, comment.getMemberId())) {
            throw new BusinessException(ReportErrorCode.CANNOT_REPORT_OWN_RESOURCE);
        }

        // 5. 신고 생성 및 저장
        CommentReport report = new CommentReport(
                comment,
                memberId,
                requestDTO.content(),
                requestDTO.reason()
        );

        commentReportRepository.save(report);
    }
}
