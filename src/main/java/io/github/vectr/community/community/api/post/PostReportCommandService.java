package io.github.vectr.community.community.api.post;

import io.github.vectr.community.community.dto.report.ReportRequestDTO;

public interface PostReportCommandService {
    // 게시글 신고 생성
    void createPostReport(Long memberId, ReportRequestDTO requestDTO);
}
