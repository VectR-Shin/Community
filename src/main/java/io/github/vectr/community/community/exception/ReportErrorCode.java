package io.github.vectr.community.community.exception;

import io.github.vectr.community.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReportErrorCode implements ErrorCode {
    DUPLICATE_REPORT_NOT_ALLOWED(
            HttpStatus.CONFLICT,
            "DUPLICATE_REPORT_NOT_ALLOWED",
            "동일한 대상에 대한 중복 신고는 불가합니다."
    ),

    CANNOT_REPORT_OWN_RESOURCE(
            HttpStatus.CONFLICT,
            "CANNOT_REPORT_OWN_RESOURCE",
            "자신의 게시글 혹은 댓글은 신고할 수 없습니다."
    ),

    REPORT_ALREADY_PROCESSED(
            HttpStatus.CONFLICT,
            "REPORT_ALREADY_PROCESSED",
            "이미 처리된 신고입니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
