package com.ssafy.house.global.common.exception;

import com.ssafy.house.global.common.base.BaseResponseStatus;

/**
 * 리소스 충돌 상황을 나타내는 예외이다.
 */
public class ResourceConflictException extends BaseException {

    /**
     * 메시지로 리소스 충돌 예외를 생성한다.
     *
     * @param message 예외 메시지
     */
    public ResourceConflictException(String message) {
        super(BaseResponseStatus.CONFLICT, message);
    }
}
