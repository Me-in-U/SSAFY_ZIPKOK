package com.ssafy.house.api.house.entity;

import java.math.BigDecimal;

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
 * 거래 완료 이력 테이블을 매핑하는 JPA 엔티티이다.
 */
@Entity
@Table(name = "house_deals_done")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HouseDealDoneEntity {

    @Id
    @Column(name = "no")
    private Integer no;

    @Column(name = "apt_seq")
    private String aptSeq;

    @Column(name = "apt_dong")
    private String aptDong;

    @Column(name = "floor")
    private String floor;

    @Column(name = "deal_year")
    private Integer dealYear;

    @Column(name = "deal_month")
    private Integer dealMonth;

    @Column(name = "deal_day")
    private Integer dealDay;

    @Column(name = "exclu_use_ar")
    private BigDecimal excluUseAr;

    @Column(name = "deal_amount")
    private String dealAmount;
}
