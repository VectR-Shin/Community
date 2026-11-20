package io.github.vectr.community.community.exception;

import io.github.vectr.community.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BoardErrorCode implements ErrorCode {
    BOARD_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "BOARD_NOT_FOUND",
            "존재하지 않는 게시판입니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
