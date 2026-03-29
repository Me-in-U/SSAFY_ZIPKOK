package com.ssafy.house.api.house.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 행정동 코드 테이블을 매핑하는 JPA 엔티티이다.
 */
@Entity
@Table(name = "dong_code")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DongCodeEntity {

    @Id
    @Column(name = "dong_code")
    private String dongCode;

    @Column(name = "sido_name")
    private String sidoName;

    @Column(name = "gugun_name")
    private String gugunName;

    @Column(name = "dong_name")
    private String dongName;
}
