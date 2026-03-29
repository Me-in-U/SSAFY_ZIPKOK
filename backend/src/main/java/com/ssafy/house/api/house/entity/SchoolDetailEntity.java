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
 * 학교 상세 테이블을 매핑하는 JPA 엔티티이다.
 */
@Entity
@Table(name = "school_detail")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SchoolDetailEntity {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "school_name")
    private String schoolName;

    @Column(name = "school_type")
    private String schoolType;
}
