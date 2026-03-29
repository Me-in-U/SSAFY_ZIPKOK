package com.ssafy.house.global.common.exception;

import com.ssafy.house.global.common.base.BaseResponseStatus;

/**
 * 로그인 인증 실패를 나타내는 예외이다.
 */
public class AuthenticationFailedException extends BaseException {

    /**
     * 메시지로 인증 실패 예외를 생성한다.
     *
     * @param message 예외 메시지
     */
    public AuthenticationFailedException(String message) {
        super(BaseResponseStatus.UNAUTHORIZED, message);
    }
}
