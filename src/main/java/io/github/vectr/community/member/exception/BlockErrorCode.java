package io.github.vectr.community.member.exception;

import io.github.vectr.community.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BlockErrorCode implements ErrorCode {
    SELF_BLOCK_NOT_ALLOWED(
            HttpStatus.BAD_REQUEST,
            "SELF_BLOCK_NOT_ALLOWED",
            "자기 자신은 차단할 수 없습니다."
    ),

    BLOCK_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "BLOCK_ALREADY_EXISTS",
            "이미 차단한 사용자입니다."
    ),

    BLOCK_LIMIT_EXCEEDED(
            HttpStatus.CONFLICT,
            "BLOCK_LIMIT_EXCEEDED",
            "차단 가능한 사용자 수를 초과했습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
