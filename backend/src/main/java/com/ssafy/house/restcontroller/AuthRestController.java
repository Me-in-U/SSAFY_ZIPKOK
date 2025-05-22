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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.house.Security.JwtUtil;
import com.ssafy.house.model.dto.Member;
import com.ssafy.house.model.dto.MemberUpdateDto;
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
    private final MemberService memberService;
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
        Member user = memberService.selectByEmail(m.getEmail());
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
            memberService.insert(member);
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

    @PutMapping("/update")
    public ResponseEntity<?> updateMember(@RequestBody MemberUpdateDto dto, Authentication authentication)
            throws SQLException {
        // 1) 토큰에서 이메일 꺼내기
        String email = authentication.getName();
        logger.warn("Current user email: {}", email);
        // 2) DB에서 Member 조회
        Member existing = memberService.selectByEmail(email);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "FAIL", "message", "회원 정보가 없습니다."));
        }

        // 3) 현재 비밀번호 검증 및 새 비밀번호 설정
        logger.warn("Current password: {}", dto.getCurrentPassword());
        logger.warn("New password: {}", dto.getNewPassword());
        logger.warn("Current password hash: {}", existing.getPassword());
        logger.warn("New password hash: {}", passwordEncoder.encode(dto.getNewPassword()));
        logger.warn("Password matches: {}", passwordEncoder.matches(dto.getCurrentPassword(), existing.getPassword()));
        if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
            if (dto.getCurrentPassword() == null ||
                    !passwordEncoder.matches(dto.getCurrentPassword(), existing.getPassword())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("status", "FAIL", "message", "현재 비밀번호가 일치하지 않습니다."));
            }
            existing.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        // 4) 이름 변경
        if (dto.getName() != null && !dto.getName().isBlank()) {
            existing.setName(dto.getName());
        }

        // 5) 저장
        memberService.update(existing);

        // 6) 응답
        return ResponseEntity.ok(Map.of("status", "SUCCESS", "user", existing));
    }

    @GetMapping("/me")
    @Operation(summary = "현재 로그인한 회원 정보 조회")
    public ResponseEntity<?> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Current authentication: {}", auth);
        String email = auth.getName();
        logger.info("Current user email: {}", email);
        Member member = memberService.selectByEmail(email);
        logger.info("Current user info: {}", member);
        if (member == null) {
            // 인증은 되었지만 DB에 회원 정보가 없을 때
            return handleFail(new RuntimeException("User not found"), HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(Map.of("status", "SUCCESS", "user", member));
    }
}