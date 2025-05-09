<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="ko">

<head>
    <title>SSAFY Home Web</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Nunito+Sans:200,300,400,700,900|Roboto+Mono:300,400,500">
    <link rel="stylesheet" href="/fonts/icomoon/style.css">
	<link rel="stylesheet" href="/fonts/flaticon/font/flaticon.css">
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
	<div class="site-loader"></div>
	<div class="site-wrap">
		<!-- 네비게이션 바 -->
		<%@ include file="/WEB-INF/views/fragments/navbar.jsp"%>
	</div>

	<!-- 회원정보 상세 보기 폼 -->
	<div class="site-blocks-cover overlay"
		style="background-image: url(/img/hero_bg_1.jpg); height: 100vh; background-size: cover;"
		data-aos="fade" data-stellar-background-ratio="0.5">
		<div class="overlay" style="background: rgba(0, 0, 0, 0.5);"></div>
		<!-- 반투명 어두운 오버레이 추가 -->
		<div class="container h-100">
			<div class="row align-items-center justify-content-center h-100">
				<div class="col-md-6 col-lg-5 bg-white border p-5 rounded shadow"
					style="position: relative; z-index: 10;">
					<h3 class="text-center mb-4 text-dark">회원정보 상세</h3>
						<section>
							<p style="color: black; font-size: 18px;">
								<strong>이름:</strong> <span class="text-dark">${loginUser.name}</span>
							</p>
							<p style="color: black; font-size: 18px;">
								<strong>이메일:</strong> <span class="text-dark">${loginUser.email}</span>
							</p>
						</section>

						<div class="form-group text-center">
							<a
								href="/member/modify"
								class="btn btn-primary text-white btn-block rounded-0">정보 수정</a>
							<a
								href="/member/delete"
								onclick="return confirm('정말로 탈퇴하시겠습니까?');"
								class="btn btn-danger text-white btn-block rounded-0">회원 탈퇴</a>
						</div>
				</div>
			</div>
		</div>
	</div>

	<!-- JS 스크립트 -->
	<script src="/js/jquery-3.3.1.min.js"></script>
	<script src="/js/jquery-migrate-3.0.1.min.js"></script>
	<script src="/js/jquery-ui.js"></script>
	<script src="/js/bootstrap.min.js"></script>
	<script src="/js/owl.carousel.min.js"></script>
	<script src="/js/jquery.magnific-popup.min.js"></script>
	<script src="/js/aos.js"></script>
	<script src="/js/main.js"></script>
</body>
</html>
