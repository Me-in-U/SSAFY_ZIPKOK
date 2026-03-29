package com.ssafy.house.global.exception;

/**
 * 현재 비밀번호 검증에 실패했을 때 발생하는 예외이다.
 */
public class CurrentPasswordMismatchException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 예외를 생성한다.
     *
     * @param message 예외 메시지
     */
    public CurrentPasswordMismatchException(String message) {
        super(message);
    }
}
