package com.ssafy.house.global.common.base;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

/**
 * 공통 API 응답 상태를 정의한다.
 */
public enum BaseResponseStatus {

    CREATED(HttpStatus.CREATED, true, 201, "리소스가 생성되었습니다."),
    SUCCESS(HttpStatus.OK, true, 200, "요청에 성공하였습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, false, 400, "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, false, 401, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, false, 403, "접근 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, false, 404, "리소스를 찾을 수 없습니다."),
    CONFLICT(HttpStatus.CONFLICT, false, 409, "리소스 충돌이 발생했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, false, 500, "서버 내부 오류가 발생했습니다.");

    final HttpStatusCode httpStatusCode;
    final boolean isSuccess;
    final int code;
    final String message;

    BaseResponseStatus(HttpStatusCode httpStatusCode, boolean isSuccess, int code, String message) {
        this.httpStatusCode = httpStatusCode;
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }

    /**
     * HTTP 상태 코드를 반환한다.
     *
     * @return HTTP 상태 코드
     */
    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    /**
     * 성공 여부를 반환한다.
     *
     * @return 성공 여부
     */
    public boolean isSuccess() {
        return isSuccess;
    }

    /**
     * 응답 코드를 반환한다.
     *
     * @return 응답 코드
     */
    public int getCode() {
        return code;
    }

    /**
     * 기본 메시지를 반환한다.
     *
     * @return 기본 메시지
     */
    public String getMessage() {
        return message;
    }
}
