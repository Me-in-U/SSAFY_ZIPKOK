package com.ssafy.house.global.common.exception;

import com.ssafy.house.global.common.base.BaseResponseStatus;

/**
 * 공통 API 예외의 기반 클래스이다.
 */
public class BaseException extends RuntimeException {

    private final BaseResponseStatus status;

    /**
     * 상태와 메시지로 예외를 생성한다.
     *
     * @param status 응답 상태
     * @param message 예외 메시지
     */
    public BaseException(BaseResponseStatus status, String message) {
        super(message);
        this.status = status;
    }

    /**
     * 응답 상태를 반환한다.
     *
     * @return 응답 상태
     */
    public BaseResponseStatus getStatus() {
        return status;
    }
}
