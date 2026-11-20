package io.github.vectr.community.community.service.report;

import io.github.vectr.community.community.api.comment.CommentReportCommandService;
import io.github.vectr.community.community.api.post.PostReportCommandService;
import io.github.vectr.community.community.api.report.ReportCommandService;
import io.github.vectr.community.community.dto.report.ReportRequestDTO;
import io.github.vectr.community.member.api.suspension.SuspensionQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportCommandServiceImpl implements ReportCommandService {

    private final SuspensionQueryService suspensionQueryService;
    private final CommentReportCommandService commentReportCommandService;
    private final PostReportCommandService postReportCommandService;

    // 리소스 신고 생성
    @Override
    public void createReport(Long memberId, ReportRequestDTO requestDTO) {
        // 1. 사용자 전역 밴 여부 확인
        suspensionQueryService.validateNotBanned(memberId);

        switch (requestDTO.targetType()) {
            case POST ->// 2.1. 게시글 신고
                    postReportCommandService.createPostReport(memberId, requestDTO);
            case COMMENT ->// 2.2. 댓글 신고
                    commentReportCommandService.createCommentReport(memberId, requestDTO);
        }
    }
}
