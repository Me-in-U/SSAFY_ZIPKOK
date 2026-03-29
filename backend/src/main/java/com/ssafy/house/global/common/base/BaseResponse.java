package com.ssafy.house.global.common.base;

import static com.ssafy.house.global.common.base.BaseResponseStatus.SUCCESS;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 모든 API 응답을 감싸는 공통 래퍼이다.
 *
 * @param <T> 실제 응답 데이터 타입
 */
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public record BaseResponse<T>(
        @JsonProperty("isSuccess") Boolean isSuccess,
        int code,
        String message,
        @JsonInclude(JsonInclude.Include.NON_NULL) T result) {

    /**
     * 성공 응답을 생성한다.
     *
     * @param result 클라이언트에 반환할 데이터
     * @param <T> 데이터 타입
     * @return 성공 응답 객체
     */
    public static <T> BaseResponse<T> onSuccess(T result) {
        return new BaseResponse<>(SUCCESS.isSuccess, SUCCESS.code, SUCCESS.message, result);
    }

    /**
     * 지정한 성공 상태로 응답을 생성한다.
     *
     * @param status 성공 상태
     * @param result 클라이언트에 반환할 데이터
     * @param <T> 데이터 타입
     * @return 성공 응답 객체
     */
    public static <T> BaseResponse<T> onSuccess(BaseResponseStatus status, T result) {
        return new BaseResponse<>(status.isSuccess, status.code, status.message, result);
    }

    /**
     * 데이터가 없는 성공 응답을 생성한다.
     *
     * @return 성공 응답 객체
     */
    public static BaseResponse<Void> onSuccess() {
        return new BaseResponse<>(SUCCESS.isSuccess, SUCCESS.code, SUCCESS.message, null);
    }

    /**
     * 실패 응답을 생성한다.
     *
     * @param status 응답 상태
     * @param <T> 데이터 타입
     * @return 실패 응답 객체
     */
    public static <T> BaseResponse<T> onFailure(BaseResponseStatus status) {
        return new BaseResponse<>(status.isSuccess, status.code, status.message, null);
    }

    /**
     * 실패 메시지를 덮어쓴 응답을 생성한다.
     *
     * @param status 응답 상태
     * @param message 사용자 정의 메시지
     * @param <T> 데이터 타입
     * @return 실패 응답 객체
     */
    public static <T> BaseResponse<T> onFailure(BaseResponseStatus status, String message) {
        return new BaseResponse<>(status.isSuccess, status.code, message, null);
    }
}
