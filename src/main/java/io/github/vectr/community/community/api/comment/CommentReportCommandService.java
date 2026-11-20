package io.github.vectr.community.community.api.comment;

import io.github.vectr.community.community.dto.report.ReportRequestDTO;

public interface CommentReportCommandService {
    // 댓글 신고 생성
    void createCommentReport(Long memberId, ReportRequestDTO requestDTO);
}
