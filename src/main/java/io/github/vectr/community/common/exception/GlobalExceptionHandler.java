package io.github.vectr.community.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final ErrorCode INTERNAL_SERVER_ERROR = CommonErrorCode.INTERNAL_SERVER_ERROR;

    // 요청 파라미터 검증 실패 예외 처리
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidation(
            HandlerMethodValidationException ex
    ) {
        String message = ex.getAllErrors()
                .get(0)
                .getDefaultMessage();

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(
                        CommonErrorCode.INVALID_REQUEST.getCode(),
                        message
                ));
    }

    // DTO 검증 실패 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(
                        CommonErrorCode.INVALID_REQUEST.getCode(),
                        message
                ));
    }

    // 비지니스 예외 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.from(errorCode));
    }

    // 예상치 못한 DB 무결성 오류 처리
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("[DataIntegrityViolationException] 예상치 못한 DB 오류 발생", ex);

        return internalServerErrorResponse();
    }

    // 권한이 없는 경우 (Filter 이후 단계에서의 접근 거부 - ex: @PreAuthorize)
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AuthorizationDeniedException ex
    ) {
        CommonErrorCode errorCode = CommonErrorCode.ACCESS_DENIED;

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.from(errorCode));
    }

    // 필수 파라미터를 생략한 경우
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(
            MissingServletRequestParameterException e) {

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(
                        CommonErrorCode.INVALID_REQUEST.getCode(),
                        e.getParameterName() + " 파라미터는 필수입니다."
                ));
    }

    // 요청 파라미터 타입 불일치
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException e) {

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(
                        CommonErrorCode.INVALID_REQUEST.getCode(),
                        e.getName() + " 파라미터의 타입이 올바르지 않습니다."
                ));
    }

    // 요청 본문 형식(Enum) 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e) {

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(
                        CommonErrorCode.INVALID_REQUEST.getCode(),
                        "요청 본문 형식이 올바르지 않습니다."
                ));
    }

    // Exception -> 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("[Exception] 예상치 못한 기타 오류 발생", ex);

        return internalServerErrorResponse();
    }

    private ResponseEntity<ErrorResponse> internalServerErrorResponse() {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR.getStatus())
                .body(ErrorResponse.from(INTERNAL_SERVER_ERROR));
    }
}