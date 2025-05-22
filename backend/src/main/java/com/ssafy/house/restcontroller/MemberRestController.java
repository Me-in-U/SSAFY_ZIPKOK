package com.ssafy.house.restcontroller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.house.model.dto.HouseRecommend;
import com.ssafy.house.model.dto.Member;
import com.ssafy.house.model.dto.MemberUpdateDto;
import com.ssafy.house.model.dto.Page;
import com.ssafy.house.model.dto.SearchCondition;
import com.ssafy.house.model.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174",
        "https://ssafy.blog",
        "https://api.ssafy.blog",
        "http://api.ssafy.blog",
        "http://192.168.204.108:5173/",
        "http://172.22.16.1:5173/" })
@Tag(name = "MemberRestController", description = "멤버 관련 기능 제공")
public class MemberRestController implements RestControllerHelper {
    private Logger logger = LoggerFactory.getLogger(AuthRestController.class);
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/{email}")
    @Operation(summary = "회원 상세 조회", description = "회원의 상세 정보를 반환한다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공"),
            @ApiResponse(responseCode = "204", description = "조회는 성공했으나 회원 정보가 없음"),
            @ApiResponse(responseCode = "500", description = "회원 정보 조회 실패"), })
    private ResponseEntity<?> memberDetail(@PathVariable String email) throws SQLException {
        try {
            Member member = memberService.selectByEmail(email);
            if (member == null) {
                return handleSuccess(Map.of("result", email), HttpStatus.NO_CONTENT);
            } else {
                return handleSuccess(Map.of("result", member));
            }

        } catch (DataAccessException e) {
            return handleFail(e);
        }
    }

    @GetMapping
    @Operation(summary = "회원 목록 조회", description = "모든 회원의 정보를 반환한다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "회원 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "회원 목록 조회 실패"), })
    private ResponseEntity<?> memberList(@ModelAttribute SearchCondition condition) {
        Map<String, String> keyMap = Map.of("1", "name", "2", "email");
        String key = condition.getKey();
        if (key != null) {
            condition.setKey(keyMap.getOrDefault(key, ""));
        }
        try {
            Page<Member> page = memberService.search(condition);
            return handleSuccess(Map.of("result", page));
        } catch (DataAccessException e) {
            return handleFail(e);
        }
    }

    @PutMapping
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

    @DeleteMapping("/{mno}")
    @Operation(summary = "회원 삭제", description = "회원 정보를 삭제한다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "회원 삭제 성공"),
            @ApiResponse(responseCode = "500", description = "회원 삭제 실패"), })
    private ResponseEntity<?> memberDelete(@PathVariable Integer mno) throws SQLException {
        try {
            memberService.delete(mno);
            return handleSuccess(Map.of("result", mno));
        } catch (DataAccessException e) {
            return handleFail(e);
        }
    }

    @PostMapping("/{mno}/favorites/{aptSeq}")
    @Operation(summary = "즐겨찾기 추가", description = "회원의 즐겨찾기에 아파트를 추가한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추가 성공"),
            @ApiResponse(responseCode = "500", description = "추가 실패")
    })
    public ResponseEntity<?> addFavorite(
            @PathVariable int mno,
            @PathVariable String aptSeq) {
        try {
            memberService.addFavorite(mno, aptSeq);
            return handleSuccess(Map.of("mno", mno, "aptSeq", aptSeq));
        } catch (DataAccessException | SQLException e) {
            return handleFail(e);
        }
    }

    @DeleteMapping("/{mno}/favorites/{aptSeq}")
    @Operation(summary = "즐겨찾기 삭제", description = "회원의 즐겨찾기에서 아파트를 삭제한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "500", description = "삭제 실패")
    })
    public ResponseEntity<?> removeFavorite(
            @PathVariable int mno,
            @PathVariable String aptSeq) {
        try {
            memberService.removeFavorite(mno, aptSeq);
            return handleSuccess(Map.of("mno", mno, "aptSeq", aptSeq));
        } catch (DataAccessException | SQLException e) {
            return handleFail(e);
        }
    }

    @GetMapping("/{mno}/favorites")
    @Operation(summary = "즐겨찾기 조회", description = "회원의 즐겨찾기 목록을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "조회 실패")
    })
    public ResponseEntity<?> listFavorites(@PathVariable int mno) {
        try {
            List<HouseRecommend> list = memberService.getFavorites(mno);
            return handleSuccess(Map.of("result", list));
        } catch (DataAccessException | SQLException e) {
            return handleFail(e);
        }
    }
}
