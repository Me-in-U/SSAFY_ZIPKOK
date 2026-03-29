package com.ssafy.house.api.house.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 아파트-학교 연계 테이블의 복합 키를 표현하는 식별자이다.
 */
@Embeddable
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HouseSchoolId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "apt_seq")
    private String aptSeq;

    @Column(name = "school_id")
    private Integer schoolId;
}
