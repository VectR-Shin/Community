package io.github.vectr.community.community.exception;

import io.github.vectr.community.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCode {
    COMMENT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "COMMENT_NOT_FOUND",
            "존재하지 않는 댓글입니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
