package io.github.vectr.community.admin.exception;

import io.github.vectr.community.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AdminErrorCode implements ErrorCode {
    INVALID_DATE_RANGE(
            HttpStatus.BAD_REQUEST,
            "INVALID_DATE_RANGE",
            "로그 생성 기간이 올바르지 않습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
