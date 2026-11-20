package io.github.vectr.community.common.exception;

public record ErrorResponse(
        String code,
        String message
) {
    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage()
        );
    }

    public static ErrorResponse from(ErrorCode errorCode, String message) {
        return new ErrorResponse(
                errorCode.getCode(),
                message
        );
    }
}