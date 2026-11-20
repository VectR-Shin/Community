package io.github.vectr.community.member.exception;

import io.github.vectr.community.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    REJOIN_RESTRICTED(
            HttpStatus.FORBIDDEN,
            "MEMBER_CREATION_FAILED",
            "탈퇴한 회원은 재가입이 불가합니다."
    ),

    MEMBER_DUPLICATED(
            HttpStatus.CONFLICT,
            "MEMBER_DUPLICATED",
            "이미 해당 방법으로 가입한 회원 정보가 존재합니다."
    ),

    MEMBER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "MEMBER_NOT_FOUND",
            "존재하지 않는 사용자입니다."
    ),

    MEMBER_ALREADY_WITHDRAWN(
            HttpStatus.CONFLICT,
            "MEMBER_ALREADY_WITHDRAWN",
            "이미 탈퇴한 사용자입니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
