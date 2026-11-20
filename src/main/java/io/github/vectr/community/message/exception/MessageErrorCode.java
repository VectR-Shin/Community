package io.github.vectr.community.message.exception;

import io.github.vectr.community.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MessageErrorCode implements ErrorCode {
    SELF_MESSAGE_NOT_ALLOWED(
            HttpStatus.BAD_REQUEST,
            "SELF_MESSAGE_NOT_ALLOWED",
            "자신에게는 메시지를 보낼 수 없습니다."
    ),

    MESSAGE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "MESSAGE_NOT_FOUND",
            "메시지가 존재하지 않습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
