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
 * 아파트 기본 정보 테이블을 매핑하는 JPA 엔티티이다.
 */
@Entity
@Table(name = "house_info")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HouseInfoEntity {

    @Id
    @Column(name = "apt_seq")
    private String aptSeq;

    @Column(name = "sgg_cd")
    private String sggCd;

    @Column(name = "umd_cd")
    private String umdCd;

    @Column(name = "umd_nm")
    private String umdNm;

    @Column(name = "jibun")
    private String jibun;

    @Column(name = "road_nm_sgg_cd")
    private String roadNmSggCd;

    @Column(name = "road_nm")
    private String roadNm;

    @Column(name = "road_nm_bonbun")
    private String roadNmBonbun;

    @Column(name = "road_nm_bubun")
    private String roadNmBubun;

    @Column(name = "apt_nm")
    private String aptNm;

    @Column(name = "build_year")
    private Integer buildYear;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;
}
