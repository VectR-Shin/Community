package io.github.vectr.community.community.api.report;

import io.github.vectr.community.community.dto.report.ReportRequestDTO;

public interface ReportCommandService {
    // 리소스 신고 생성
    void createReport(Long memberId, ReportRequestDTO requestDTO);
}
