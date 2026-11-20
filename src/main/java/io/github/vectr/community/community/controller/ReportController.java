package io.github.vectr.community.community.controller;

import io.github.vectr.community.community.api.comment.CommentReportCommandService;
import io.github.vectr.community.community.api.post.PostReportCommandService;
import io.github.vectr.community.community.api.report.ReportCommandService;
import io.github.vectr.community.community.dto.report.ReportRequestDTO;
import io.github.vectr.community.config.security.oauth2.CustomOidcUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportCommandService reportCommandService;

    // 리소스에 대한 신고 생성
    @PostMapping("/reports")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createReport(
            @AuthenticationPrincipal CustomOidcUser principal,
            @Valid @RequestBody ReportRequestDTO requestDTO
    ) {
        reportCommandService.createReport(principal.getMemberId(), requestDTO);
    }
}
