package com.ssafy.house.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 회원 정보 수정용 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberUpdateDto {
    private int mno;
    private String currentPassword;// 검증용 현재 비밀번호 (현재 비밀번호를 변경할 때만 필요)
    private String newPassword;// 변경할 비밀번호
    private String name; // 변경할 사용자 이름
}
