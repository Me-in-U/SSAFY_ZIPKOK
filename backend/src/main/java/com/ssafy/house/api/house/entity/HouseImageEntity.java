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
 * 아파트 이미지 테이블을 매핑하는 JPA 엔티티이다.
 */
@Entity
@Table(name = "house_image")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HouseImageEntity {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "apt_seq")
    private String aptSeq;

    @Column(name = "img_path")
    private String imgPath;
}
