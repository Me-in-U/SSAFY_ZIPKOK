package com.ssafy.house.api.house.entity;

import java.sql.Timestamp;

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
 * 매물 테이블을 매핑하는 JPA 엔티티이다.
 */
@Entity
@Table(name = "house_deal")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HouseDealEntity {

    @Id
    @Column(name = "deal_id")
    private Integer dealId;

    @Column(name = "apt_seq")
    private String aptSeq;

    @Column(name = "listing_name")
    private String listingName;

    @Column(name = "trade_type")
    private String tradeType;

    @Column(name = "price")
    private Long price;

    @Column(name = "property_type")
    private String propertyType;

    @Column(name = "spec")
    private String spec;

    @Column(name = "description")
    private String description;

    @Column(name = "confirmed_at")
    private Timestamp confirmedAt;

    @Column(name = "deposit")
    private Long deposit;

    @Column(name = "monthly_rent")
    private Integer monthlyRent;
}
