<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">

<head>
    <title>SSAFY Home Web</title>
    <meta charset="utf-8">
    <meta name="viewport"
        content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Nunito+Sans:200,300,400,700,900|Roboto+Mono:300,400,500">
    <link rel="stylesheet" href="/fonts/icomoon/style.css">
    <link rel="stylesheet" href="/css/bootstrap.min.css">
    <link rel="stylesheet" href="/css/magnific-popup.css">
    <link rel="stylesheet" href="/css/jquery-ui.css">
    <link rel="stylesheet" href="/css/owl.carousel.min.css">
    <link rel="stylesheet" href="/css/owl.theme.default.min.css">
    <link rel="stylesheet" href="/css/bootstrap-datepicker.css">
    <link rel="stylesheet" href="/css/mediaelementplayer.css">
    <link rel="stylesheet" href="/css/animate.css">
    <link rel="stylesheet" href="/fonts/flaticon/font/flaticon.css">
    <link rel="stylesheet" href="/css/fl-bigmug-line.css">
    <link rel="stylesheet" href="/css/aos.css">
    <link rel="stylesheet" href="/css/style.css">
</head>

<body>
	<div class="site-loader"></div>
	<div class="site-wrap">
		<!-- 네비게이션 바 -->
		<%@ include file="/WEB-INF/views/fragments/navbar.jsp"%>
	</div>

	<!-- 로그인 폼 -->
	<div class="site-blocks-cover overlay"
		style="background-image: url(/img/hero_bg_1.jpg); height: 100vh;"
		data-aos="fade" data-stellar-background-ratio="0.5">
		<div class="container h-100">
			<div class="row align-items-center justify-content-center h-100">
				<div class="col-md-6 col-lg-5 bg-white border p-5 rounded shadow"
					style="position: relative; z-index: 10;">
					<h3 class="text-center mb-4">로그인</h3>
						<form id="loginForm">
						<div class="form-group">
							<label class="font-weight-bold" for="email">이메일</label> <input
								type="email" id="email" name="email" class="form-control"
								placeholder="이메일 입력" required value="${cookie.rememberMe.value}">
						</div>
						<div class="form-group">
							<label class="font-weight-bold" for="password">비밀번호</label> <input
								type="password" id="password" name="password"
								class="form-control" placeholder="비밀번호 입력" required>
						</div>
						<div class="form-group text-center">
							<div class="form-check">
								<input type="checkbox" value="on" name="remember-me"
									id="rememberMe" class="form-check-input"
									${cookie.rememberMe!=null?"checked":"" } /> <label
									for="remember-me" class="form-check-label">id 기억하기</label>
							</div>
							<button type="submit" class="btn btn-success text-white btn-block rounded-0">로그인</button>
						</div>
					</form>
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
	<script>
    // 초기 로드 시 저장된 이메일 불러오기
    const savedEmail = localStorage.getItem('rememberEmail');
    if (savedEmail) {
        document.getElementById('email').value = savedEmail;
        document.getElementById('rememberMe').checked = true;
    }

    document.getElementById('loginForm').addEventListener('submit', async e => {
        e.preventDefault();
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
		console.log('로그인 시도 - email:', email, 'password:', password);
        const response = await fetch('/api/v1/members/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        const result = await response.json();
		if (response.ok && result.status === 'SUCCESS') {
            const token = result.data.token;
            // JWT 토큰 저장
            localStorage.setItem('jwtToken', token);
            // ID 기억하기
            if (document.getElementById('rememberMe').checked) {
                localStorage.setItem('rememberEmail', email);
            } else {
                localStorage.removeItem('rememberEmail');
            }
            // 메인 페이지로 이동
            window.location.href = '/';
        } else {
            alert(result.error || '로그인에 실패했습니다.');
        }
    });
    </script>
</body>
</html>

