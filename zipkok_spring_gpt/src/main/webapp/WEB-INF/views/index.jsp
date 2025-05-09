<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<title>SSAFY Home Web</title>
<meta charset="UTF-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">

<link rel="stylesheet"
	href="https://fonts.googleapis.com/css?family=Nunito+Sans:200,300,400,700,900|Roboto+Mono:300,400,500">
<link rel="stylesheet" href="/fonts/icomoon/style.css">
<link rel="stylesheet" href="/css/bootstrap.min.css">
<link rel="stylesheet" href="/css/magnific-popup.css">
<link rel="stylesheet" href="/css/jquery-ui.css">
<link rel="stylesheet" href="/css/owl.carousel.min.css">
<link rel="stylesheet" href="/css/owl.theme.default.min.css">
<link rel="stylesheet" href="/css/bootstrap-datepicker.css">
<link rel="stylesheet" href="/css/mediaelementplayer.css">
<link rel="stylesheet" href="/css/animate.css">
<link rel="stylesheet" href="/css/aos.css">
<link rel="stylesheet" href="/css/style.css">
</head>
<body>
	<%@ include file="/WEB-INF/views/fragments/chatbot.jsp"%>
	<div class="site-loader"></div>
	<div class="site-wrap">
		<!-- 네비게이션 바 -->
		<%@ include file="/WEB-INF/views/fragments/navbar.jsp"%>
	</div>

	<!-- 슬라이더 -->
	<div class="slide-one-item home-slider owl-carousel">
		<div class="site-blocks-cover overlay"
			style="background-image: url(/img/hero_bg_1.jpg);"
			data-aos="fade"
			data-stellar-background-ratio="0.5">
			<div class="container">
				<div
					class="row align-items-center justify-content-center text-center">
					<div class="col-md-10">
						<span class="d-inline-block bg-success text-white px-3 mb-3 property-offer-type rounded">매매</span>
						<h1 class="mb-2">반포 자이아파트</h1>
						<p class="mb-5">
							<strong class="h2 text-success font-weight-bold">30억</strong>
						</p>
						<p>
							<a href="/" class="btn btn-white btn-outline-white py-3 px-5 rounded-0 btn-2">평생 일해도 못산다</a>
						</p>
					</div>
				</div>
			</div>
		</div>
		<div class="site-blocks-cover overlay"
			style="background-image: url(/img/hero_bg_2.jpg);"
			data-aos="fade"
			data-stellar-background-ratio="0.5">
			<div class="container">
				<div
					class="row align-items-center justify-content-center text-center">
					<div class="col-md-10">
						<span class="d-inline-block bg-danger text-white px-3 mb-3 property-offer-type rounded">전세</span>
						<h1 class="mb-2">부산 엘시티 더샵</h1>
						<p class="mb-5">
							<strong class="h2 text-success font-weight-bold">20억</strong>
						</p>
						<p>
							<a href="#"
								class="btn btn-white btn-outline-white py-3 px-5 rounded-0 btn-2">평생
								일해도 못산다</a>
						</p>
					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- 법정동 서비스 -->
	<div class="site-section site-section-sm pb-0">
		<div class="container">
			<div class="row">
				<form class="form-search col-md-12" style="margin-top: -100px;">
					<h3 class="d-inline-block text-white px-3 mb-3 property-offer-type rounded fs-4">법정동 기반의 서비스(HappyHouse):</h3class=class=>
					<div class="row align-items-end">
						<div class="col-md-3">
							<label for="list-types">시도 선택: </label>
							<div class="select-wrap">
								<span class="icon icon-arrow_drop_down"></span>
								<select
									id="sido" name="list-types" class="form-control d-block rounded-0">
									<option value="" selected disabled>시도 선택</option>
								</select>
							</div>
						</div>
						<div class="col-md-3">
							<label for="offer-types">구군 선택: </label>
							<div class="select-wrap">
								<span class="icon icon-arrow_drop_down"></span>
								<select id="gugun" name="offer-types" class="form-control d-block rounded-0">
									<option value="" selected disabled>구군 선택</option>
								</select>
							</div>
						</div>
						<div class="col-md-3">
							<label for="select-city">읍면동 선택</label>
							<div class="select-wrap">
								<span class="icon icon-arrow_drop_down"></span> <select
									id="dong" name="select-city"
									class="form-control d-block rounded-0">
									<option value="" selected disabled>읍면동 선택</option>
								</select>
							</div>
						</div>
						<div class="col-md-3">
							<label for="dealYmd">년 월 선택</label>
							<div class="select-wrap">
								<input class="form-control d-block rounded-0" type="month"
									id="dealYmd" />
							</div>
						</div>
						<div class="col-md-3">
							<input id="btn_apt_search" type="button"
								class="btn-apt-search btn btn-success text-white btn-block rounded-0"
								value="아파트 조회">
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- 지도 및 아파트 정보 -->
	<div class="site-section">
		<div class="container">
			<div class="row justify-content-center">
				<div id="map" style="width: 100%; height: 500px;"></div>
			</div>
			<div id="apartment-info" class="row mt-4">
				<!-- 아파트 정보 카드 -->
			</div>
		</div>
	</div>

	<!-- JavaScript -->
	<script>
    const apiKeys = {
        sgisServiceId: "${apiKeySgisServiceId}",
        sgisSecurity: "${apiKeySgisSecurity}",
        vworld: "${apiKeyVworld}",
        data: "${apiKeyData}"
    };
	</script>
	<script src="https://sgisapi.kostat.go.kr/OpenAPI3/auth/javascriptAuth?consumer_key=${apiKeySgisServiceId}"></script>
	<script src="/js/jquery-3.3.1.min.js"></script>
	<script src="/js/jquery-migrate-3.0.1.min.js"></script>
	<script src="/js/jquery-ui.js"></script>
	<script src="/js/bootstrap.min.js"></script>
	<script src="/js/owl.carousel.min.js"></script>
	<script src="/js/jquery.magnific-popup.min.js"></script>
	<script src="/js/aos.js"></script>
	<script src="/js/main.js"></script>
	<script src="/js/keys.js"></script>
	<script src="/js/common.js"></script>
	<script src="/js/kostat.js"></script>
	<script src="/js/happyHouse.js"></script>
	<script>
		const init = async () => {
			updateSido();
			updateMap([
				{
					address: "서울특별시 강남구 테헤란로 212",
					utmk: await getCoords("서울특별시 강남구 테헤란로 212"),
					label: "멀티캠퍼스",
					floor: "50층",
					buildYear: "2088년",
					dealAmount: "1조",
					buyerGbn: "개인"
				},
			]);
		};
		init();
  </script>
</body>
</html>