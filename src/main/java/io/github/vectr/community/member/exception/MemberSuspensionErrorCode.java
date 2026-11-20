package io.github.vectr.community.member.exception;

import io.github.vectr.community.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberSuspensionErrorCode implements ErrorCode {
    MEMBER_BANNED(
            HttpStatus.UNAUTHORIZED,
            "MEMBER_BANNED",
            "전역 밴 사용자입니다."
    ),

    MEMBER_NOT_SUSPENDED(
            HttpStatus.NOT_FOUND,
            "MEMBER_NOT_SUSPENDED",
            "현재 전역 활동 정지 상태가 아닌 사용자입니다."
    ),

    MEMBER_SUSPENSION_ALREADY_LIFTED(
            HttpStatus.CONFLICT,
            "MEMBER_SUSPENSION_ALREADY_LIFTED",
            "이미 해제된 전역 활동 정지입니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
