package com.ssafy.house.api.house.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 아파트와 학교 간 거리 정보를 매핑하는 JPA 엔티티이다.
 */
@Entity
@Table(name = "house_school")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HouseSchoolEntity {

    @EmbeddedId
    private HouseSchoolId id;

    @Column(name = "distance")
    private String distance;
}
