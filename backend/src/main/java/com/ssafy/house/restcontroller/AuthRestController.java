package com.ssafy.house.restcontroller;

import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.house.Security.JwtUtil;
import com.ssafy.house.model.dto.Member;
import com.ssafy.house.model.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174", "https://ssafy.blog",
        "http://api.ssafy.blog",
        "http://192.168.204.108:5173/",
        "http://172.22.16.1:5173/", "http://localhost:8080/" })
@Tag(name = "AuthRestController", description = "로그인 인증 관련 기능 제공")
public class AuthRestController implements RestControllerHelper {
    private final MemberService mService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private Logger logger = LoggerFactory.getLogger(AuthRestController.class);

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일/비밀번호로 로그인 처리")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> login(@RequestBody Member m) {
        Member user = mService.selectByEmail(m.getEmail());
        if (user == null ||
                !passwordEncoder.matches(m.getPassword(), user.getPassword())) {
            return handleFail(new RuntimeException("Invalid credentials"),
                    HttpStatus.UNAUTHORIZED);
        }
        String token = jwtUtil.generateToken(user.getEmail());
        return handleSuccess(Map.of(
                "token", token,
                "user", user));
    }

    @PostMapping("/regist")
    @Operation(summary = "회원 가입", description = "회원 가입을 처리한다. 회원 정보는 json 문자열로 전달받는다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원 가입 성공"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 회원"),
            @ApiResponse(responseCode = "500", description = "회원 가입 실패(서버오류)"),
    })
    private ResponseEntity<?> registMember(@RequestBody Member member) throws SQLException {
        try {
            member.setPassword(passwordEncoder.encode(member.getPassword()));
            mService.insert(member);
            return handleSuccess(Map.of("member", member), HttpStatus.CREATED);
        } // 중복 키(Unique constraint) 위반 시 409 Conflict
        catch (DataIntegrityViolationException e) {
            // 로그 남기기
            logger.warn("회원 가입 중 중복 오류: {}", e.getMessage());
            return handleFail(e, HttpStatus.CONFLICT);
        }
        // 그 외 DB 접근 에러는 500
        catch (DataAccessException e) {
            logger.error("DB 에러 발생", e);
            return handleFail(e);
        }
    }

    @PostMapping("/reset-password")
    @Operation(summary = "비밀번호 초기화", description = "이메일로 비밀번호를 초기화하고 임시 비밀번호를 반환한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "초기화 성공"),
            @ApiResponse(responseCode = "404", description = "회원 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        try {
            Member member = mService.selectByEmail(email);
            if (member == null) {
                return handleFail(
                        new RuntimeException("Member not found"),
                        HttpStatus.NOT_FOUND);
            }
            // 임시 비밀번호 생성 (예: 8자리 랜덤 알파벳+숫자)
            String tempPwd = RandomStringUtils.randomAlphanumeric(8);
            // BCryptPasswordEncoder 등으로 암호화 후 저장
            member.setPassword(passwordEncoder.encode(tempPwd));
            mService.update(member);
            return handleSuccess(
                    Map.of(
                            "email", email,
                            "temporaryPassword", tempPwd));
        } catch (DataAccessException | SQLException e) {
            return handleFail(e);
        }
    }

    @GetMapping("/me")
    @Operation(summary = "현재 로그인한 회원 정보 조회")
    public ResponseEntity<?> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Current authentication: {}", auth);
        String email = auth.getName();
        logger.info("Current user email: {}", email);
        Member member = mService.selectByEmail(email);
        logger.info("Current user info: {}", member);
        if (member == null) {
            // 인증은 되었지만 DB에 회원 정보가 없을 때
            return handleFail(new RuntimeException("User not found"), HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(Map.of("status", "SUCCESS", "user", member));
    }
}