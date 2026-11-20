package io.github.vectr.community.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    INVALID_REQUEST(
            HttpStatus.BAD_REQUEST,
            "INVALID_REQUEST",
            "잘못된 요청입니다."
    ),

    UNAUTHENTICATED(
            HttpStatus.UNAUTHORIZED,
            "UNAUTHENTICATED",
            "인증되지 않은 사용자입니다."
    ),

    ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "ACCESS_DENIED",
            "해당 요청에 대한 권한이 없습니다."
    ),

    RESOURCE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "RESOURCE_NOT_FOUND",
            "존재하지 않는 리소스입니다."
    ),

    RESOURCE_CONFLICT(
            HttpStatus.CONFLICT,
            "RESOURCE_CONFLICT",
            "요청이 현재 리소스 상태와 충돌합니다."
    ),

    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_SERVER_ERROR",
            "서버 내부 오류가 발생했습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}