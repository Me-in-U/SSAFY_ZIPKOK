package com.ssafy.house.global.common.exception;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.ssafy.house.global.common.base.BaseResponse;
import com.ssafy.house.global.common.base.BaseResponseStatus;
import com.ssafy.house.global.exception.CurrentPasswordMismatchException;

import lombok.extern.slf4j.Slf4j;

/**
 * 컨트롤러 전반의 예외를 표준 BaseResponse 형식으로 변환하는 전역 예외 처리기이다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 공통 API 예외를 처리한다.
     *
     * @param e 공통 API 예외
     * @return 표준 실패 응답
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<Void>> handleBaseException(BaseException e) {
        log.warn("BaseException: {}", e.getMessage());
        return ResponseEntity.status(e.getStatus().getHttpStatusCode())
                .body(BaseResponse.onFailure(e.getStatus(), e.getMessage()));
    }

    /**
     * 리소스 미존재 예외를 처리한다.
     *
     * @param e 리소스 미존재 예외
     * @return 표준 실패 응답
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<BaseResponse<Void>> handleNoSuchElementException(NoSuchElementException e) {
        log.warn("NoSuchElementException: {}", e.getMessage());
        return ResponseEntity.status(BaseResponseStatus.NOT_FOUND.getHttpStatusCode())
                .body(BaseResponse.onFailure(BaseResponseStatus.NOT_FOUND, e.getMessage()));
    }

    /**
     * 현재 비밀번호 불일치 예외를 처리한다.
     *
     * @param e 현재 비밀번호 불일치 예외
     * @return 표준 실패 응답
     */
    @ExceptionHandler(CurrentPasswordMismatchException.class)
    public ResponseEntity<BaseResponse<Void>> handleCurrentPasswordMismatchException(
            CurrentPasswordMismatchException e) {
        log.warn("CurrentPasswordMismatchException: {}", e.getMessage());
        return ResponseEntity.status(BaseResponseStatus.FORBIDDEN.getHttpStatusCode())
                .body(BaseResponse.onFailure(BaseResponseStatus.FORBIDDEN, e.getMessage()));
    }

    /**
     * 스프링의 상태 기반 예외를 처리한다.
     *
     * @param e 상태 기반 예외
     * @return 표준 실패 응답
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<BaseResponse<Void>> handleResponseStatusException(ResponseStatusException e) {
        BaseResponseStatus status = mapStatus(e.getStatusCode());
        String message = e.getReason() != null ? e.getReason() : status.getMessage();
        log.warn("ResponseStatusException: status={} message={}", e.getStatusCode(), message);
        return ResponseEntity.status(e.getStatusCode())
                .body(BaseResponse.onFailure(status, message));
    }

    /**
     * 잘못된 요청 본문 또는 검증 실패를 처리한다.
     *
     * @param e 잘못된 요청 예외
     * @return 표준 실패 응답
     */
    @ExceptionHandler({
            IllegalArgumentException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<BaseResponse<Void>> handleBadRequestException(Exception e) {
        log.warn("BadRequestException: {}", e.getMessage());
        return ResponseEntity.status(BaseResponseStatus.BAD_REQUEST.getHttpStatusCode())
                .body(BaseResponse.onFailure(BaseResponseStatus.BAD_REQUEST, "잘못된 요청입니다."));
    }

    /**
     * 인증 실패 예외를 처리한다.
     *
     * @param e 인증 실패 예외
     * @return 표준 실패 응답
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<BaseResponse<Void>> handleAuthenticationException(AuthenticationException e) {
        log.warn("AuthenticationException: {}", e.getMessage());
        return ResponseEntity.status(BaseResponseStatus.UNAUTHORIZED.getHttpStatusCode())
                .body(BaseResponse.onFailure(BaseResponseStatus.UNAUTHORIZED));
    }

    /**
     * 인가 실패 예외를 처리한다.
     *
     * @param e 인가 실패 예외
     * @return 표준 실패 응답
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("AccessDeniedException: {}", e.getMessage());
        return ResponseEntity.status(BaseResponseStatus.FORBIDDEN.getHttpStatusCode())
                .body(BaseResponse.onFailure(BaseResponseStatus.FORBIDDEN));
    }

    /**
     * 데이터 충돌 예외를 처리한다.
     *
     * @param e 데이터 충돌 예외
     * @return 표준 실패 응답
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse<Void>> handleDataIntegrityViolationException(
            DataIntegrityViolationException e) {
        log.warn("DataIntegrityViolationException: {}", e.getMessage());
        return ResponseEntity.status(BaseResponseStatus.CONFLICT.getHttpStatusCode())
                .body(BaseResponse.onFailure(BaseResponseStatus.CONFLICT, "리소스 충돌이 발생했습니다."));
    }

    /**
     * 데이터 접근 및 SQL 예외를 처리한다.
     *
     * @param e 데이터 접근 예외
     * @return 표준 실패 응답
     */
    @ExceptionHandler({SQLException.class, DataAccessException.class})
    public ResponseEntity<BaseResponse<Void>> handleDataAccessException(Exception e) {
        log.error("DataAccessException", e);
        return ResponseEntity.status(BaseResponseStatus.INTERNAL_SERVER_ERROR.getHttpStatusCode())
                .body(BaseResponse.onFailure(BaseResponseStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * 처리되지 않은 예외를 처리한다.
     *
     * @param e 처리되지 않은 예외
     * @return 표준 실패 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleException(Exception e) {
        log.error("Unhandled exception", e);
        return ResponseEntity.status(BaseResponseStatus.INTERNAL_SERVER_ERROR.getHttpStatusCode())
                .body(BaseResponse.onFailure(BaseResponseStatus.INTERNAL_SERVER_ERROR));
    }

    private BaseResponseStatus mapStatus(HttpStatusCode statusCode) {
        if (statusCode.value() == HttpStatus.UNAUTHORIZED.value()) {
            return BaseResponseStatus.UNAUTHORIZED;
        }
        if (statusCode.value() == HttpStatus.FORBIDDEN.value()) {
            return BaseResponseStatus.FORBIDDEN;
        }
        if (statusCode.value() == HttpStatus.NOT_FOUND.value()) {
            return BaseResponseStatus.NOT_FOUND;
        }
        if (statusCode.value() == HttpStatus.BAD_REQUEST.value()) {
            return BaseResponseStatus.BAD_REQUEST;
        }
        if (statusCode.value() == HttpStatus.CONFLICT.value()) {
            return BaseResponseStatus.CONFLICT;
        }
        return BaseResponseStatus.INTERNAL_SERVER_ERROR;
    }
}
