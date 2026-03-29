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
 * 아파트 상세 통계 테이블을 매핑하는 JPA 엔티티이다.
 */
@Entity
@Table(name = "house_detail")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HouseDetailEntity {

    @Id
    @Column(name = "apt_seq")
    private String aptSeq;

    @Column(name = "area_min")
    private Double areaMin;

    @Column(name = "area_max")
    private Double areaMax;

    @Column(name = "trade_price_min")
    private Long tradePriceMin;

    @Column(name = "trade_price_max")
    private Long tradePriceMax;

    @Column(name = "jeonse_price_min")
    private Long jeonsePriceMin;

    @Column(name = "jeonse_price_max")
    private Long jeonsePriceMax;

    @Column(name = "last_trade_detail")
    private String lastTradeDetail;

    @Column(name = "school_id")
    private Integer schoolId;
}
