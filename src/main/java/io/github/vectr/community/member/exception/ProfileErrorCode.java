package io.github.vectr.community.member.exception;

import io.github.vectr.community.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProfileErrorCode implements ErrorCode {
    INVALID_PROFILE_UPDATE_REQUEST(
            HttpStatus.BAD_REQUEST,
            "INVALID_PROFILE_UPDATE_REQUEST",
            "프로필 업데이트 정보가 잘못되었습니다."
    ),

    DUPLICATE_NICKNAME(
            HttpStatus.CONFLICT,
            "DUPLICATE_NICKNAME",
            "중복된 닉네임입니다."
    ),

    PROFILE_GENERATION_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "PROFILE_GENERATION_FAILED",
            "사용자 프로필 생성에 실패했습니다."
    ),

    PROFILE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PROFILE_NOT_FOUND",
            "사용자의 프로필이 존재하지 않습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
