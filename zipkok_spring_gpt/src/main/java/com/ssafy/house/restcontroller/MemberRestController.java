package com.ssafy.house.restcontroller;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.house.model.dto.Member;
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
@Tag(name = "MemberRestController", description = "회원 가입을 위한 기능 제공")
public class MemberRestController implements RestControllerHelper {

    private final MemberService mService;

    @PostMapping
    @Operation(summary = "회원 가입", description = "회원 가입을 처리한다. 회원 정보는 json 문자열로 전달받는다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원 가입 성공"),
            @ApiResponse(responseCode = "500", description = "회원 가입 실패(서버오류)"),
    })
    private ResponseEntity<?> registMember(@RequestBody Member member) throws SQLException {
        try {
            mService.insert(member);
            return handleSuccess(Map.of("member", member), HttpStatus.CREATED);
        } catch (DataAccessException e) {
            System.out.println(e.getCause());
            return handleFail(e);
        }
    }
}
