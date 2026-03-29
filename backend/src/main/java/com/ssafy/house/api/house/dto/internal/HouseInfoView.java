package com.ssafy.house.api.house.dto.internal;

/**
 * 아파트 기본 정보의 repository 전용 조회 모델이다.
 *
 * @param aptSeq 아파트 식별자
 * @param sggCd 시군구 코드
 * @param umdCd 읍면동 코드
 * @param umdNm 읍면동 이름
 * @param jibun 지번
 * @param roadNmSggCd 도로명 시군구 코드
 * @param roadNm 도로명
 * @param roadNmBonbun 도로명 본번
 * @param roadNmBubun 도로명 부번
 * @param aptNm 아파트명
 * @param buildYear 준공 연도
 * @param latitude 위도
 * @param longitude 경도
 */
public record HouseInfoView(
        String aptSeq,
        String sggCd,
        String umdCd,
        String umdNm,
        String jibun,
        String roadNmSggCd,
        String roadNm,
        String roadNmBonbun,
        String roadNmBubun,
        String aptNm,
        Integer buildYear,
        String latitude,
        String longitude) {
}

