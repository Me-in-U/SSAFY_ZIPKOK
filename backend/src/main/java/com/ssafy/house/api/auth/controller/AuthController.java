package com.ssafy.house.api.auth.controller;

import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.house.api.auth.dto.request.LoginRequest;
import com.ssafy.house.api.auth.dto.request.RegisterRequest;
import com.ssafy.house.api.auth.dto.response.LoginResponse;
import com.ssafy.house.api.auth.dto.response.RegisterResponse;
import com.ssafy.house.api.auth.service.AuthService;
import com.ssafy.house.global.common.base.BaseResponse;
import com.ssafy.house.global.common.base.BaseResponseStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 신규 인증 API를 제공하는 컨트롤러이다.
 */
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "회원 인증 기능")
public class AuthController {

    private final AuthService authService;

    /**
     * 이메일과 비밀번호로 로그인한다.
     *
     * @param request 로그인 요청 데이터
     * @return 로그인 응답
     */
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호를 검증해 JWT 토큰을 발급한다.")
    public BaseResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return BaseResponse.onSuccess(authService.login(request));
    }

    /**
     * 회원가입을 수행한다.
     *
     * @param request 회원가입 요청 데이터
     * @return 회원가입 응답
     */
    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "새 회원을 생성하고 생성된 회원 정보를 반환한다.")
    public ResponseEntity<BaseResponse<RegisterResponse>> register(@RequestBody RegisterRequest request)
            throws SQLException {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.onSuccess(BaseResponseStatus.CREATED, response));
    }
}
