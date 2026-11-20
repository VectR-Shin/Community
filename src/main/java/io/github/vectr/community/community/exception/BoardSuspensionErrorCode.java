package io.github.vectr.community.community.exception;

import io.github.vectr.community.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BoardSuspensionErrorCode implements ErrorCode {
    BOARD_MEMBER_BANNED(
            HttpStatus.UNAUTHORIZED,
            "BOARD_MEMBER_BANNED",
            "게시판 이용 제한 사용자입니다."
    ),

    MEMBER_NOT_BOARD_SUSPENDED(
            HttpStatus.NOT_FOUND,
            "MEMBER_NOT_BOARD_SUSPENDED",
            "현재 게시판 정지 상태가 아닌 사용자입니다."
    ),

    MEMBER_BOARD_SUSPENSION_ALREADY_LIFTED(
            HttpStatus.CONFLICT,
            "MEMBER_BOARD_SUSPENSION_ALREADY_LIFTED",
            "만료된 게시판 활동 정지입니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
