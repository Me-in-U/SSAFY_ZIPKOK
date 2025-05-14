package com.ssafy.house.restcontroller;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.house.model.dto.Member;
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
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174", "https://ssafy.blog",
        "http://192.168.204.108:5173/",
        "http://172.22.16.1:5173/", "http://localhost:8080/" })
@Tag(name = "MemberRestController", description = "멤버 관련 기능 제공")
public class MemberRestController implements RestControllerHelper {

    private final MemberService mService;

    @GetMapping("/{email}")
    @Operation(summary = "회원 상세 조회", description = "회원의 상세 정보를 반환한다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공"),
            @ApiResponse(responseCode = "204", description = "조회는 성공했으나 회원 정보가 없음"),
            @ApiResponse(responseCode = "500", description = "회원 정보 조회 실패"), })
    private ResponseEntity<?> memberDetail(@PathVariable String email) throws SQLException {
        try {
            Member member = mService.selectByEmail(email);
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
            Page<Member> page = mService.search(condition);
            return handleSuccess(Map.of("result", page));
        } catch (DataAccessException e) {
            return handleFail(e);
        }
    }

    @PutMapping
    @Operation(summary = "회원 정보 수정", description = "회원 정보를 수정한다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
            @ApiResponse(responseCode = "500", description = "회원 정보 수정 실패"), })
    private ResponseEntity<?> memberModify(@RequestBody Member member) throws SQLException {
        try {
            mService.update(member);
            return handleSuccess(Map.of("result", member));
        } catch (DataAccessException e) {
            return handleFail(e);
        }
    }

    @DeleteMapping("/{mno}")
    @Operation(summary = "회원 삭제", description = "회원 정보를 삭제한다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "회원 삭제 성공"),
            @ApiResponse(responseCode = "500", description = "회원 삭제 실패"), })
    private ResponseEntity<?> memberDelete(@PathVariable Integer mno) throws SQLException {
        try {
            mService.delete(mno);
            return handleSuccess(Map.of("result", mno));
        } catch (DataAccessException e) {
            return handleFail(e);
        }
    }
}
