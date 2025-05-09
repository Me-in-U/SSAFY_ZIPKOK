<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="root" value="${pageContext.servletContext.contextPath }" />
<!DOCTYPE html>
<html lang="ko">

<head>
    <title>SSAFY Home Web</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Nunito+Sans:200,300,400,700,900|Roboto+Mono:300,400,500">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/fonts/icomoon/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/magnific-popup.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/jquery-ui.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/owl.carousel.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/owl.theme.default.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap-datepicker.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/mediaelementplayer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/animate.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/fonts/flaticon/font/flaticon.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/aos.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>

<body>

    <div class="site-loader"></div>

    <div class="site-wrap">
        <!-- 모바일 메뉴 -->
        <div class="site-mobile-menu">
            <div class="site-mobile-menu-header">
                <div class="site-mobile-menu-close mt-3">
                    <span class="icon-close2 js-menu-toggle"></span>
                </div>
            </div>
            <div class="site-mobile-menu-body"></div>
        </div>

        <!-- Navbar -->
        <div class="site-navbar mt-4">
            <div class="container py-1">
                <div class="row align-items-center">
                    <div class="col-8 col-md-8 col-lg-4">
                        <h1 class="mb-0">
                            <a href="index.jsp" class="text-white h2 mb-0"> <strong>SSAFY
                                    Home<span class="text-danger">.</span>
                            </strong></a>
                        </h1>
                    </div>
                    <div class="col-4 col-md-4 col-lg-8">
                        <nav class="site-navigation text-right text-md-right"
                            role="navigation">
                            <div class="d-inline-block d-lg-none ml-md-0 mr-auto py-3">
                                <a href="#" class="site-menu-toggle js-menu-toggle text-white"><span
                                    class="icon-menu h3"></span></a>
                            </div>

                            <ul class="site-menu js-clone-nav d-none d-lg-block">
                                <li class="active"><a href="index.jsp">메인화면</a></li>
                                <li><a href="${root }/member?action=mypage">마이페이지</a></li>
                                <li><c:if test="${empty loginUser }">
                                        <a href="${root }/member?action=login-form">로그인</a>
                                        <a href="${root }/member?action=regist-member-form">회원가입</a>
                                    </c:if></li>
                                <li><c:if test="${!empty loginUser }">
                                        <a href="${root }/member?action=logout">로그아웃</a>
                                    </c:if></li>
                            </ul>
                        </nav>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 회원정보 상세 보기 폼 -->
    <div class="site-blocks-cover overlay" style="background-image: url(${pageContext.request.contextPath}/assets/img/hero_bg_1.jpg); height: 100vh; background-size: cover;" data-aos="fade" data-stellar-background-ratio="0.5">
        <div class="overlay" style="background: rgba(0, 0, 0, 0.5);"></div> <!-- 반투명 어두운 오버레이 추가 -->
        <div class="container h-100">
            <div class="row align-items-center justify-content-center h-100">
                <div class="col-md-6 col-lg-5 bg-white border p-5 rounded shadow" style="position: relative; z-index: 10;">
                    <h3 class="text-center mb-4 text-dark">회원정보 상세</h3>
                    <form action="${pageContext.request.contextPath}/member" method="post" disabled>
                        <input type="hidden" name="action" value="view" />

						<section>
							<p style="color: black; font-size: 18px;"><strong>이름:</strong> <span class="text-dark">${loginUser.name}</span></p>
							<p style="color: black; font-size: 18px;"><strong>이메일:</strong> <span class="text-dark">${loginUser.email}</span></p>


                        </section>

                        <div class="form-group text-center">
                            <a href="${root }/auth?action=member-modify-form&email=${loginUser.email}"  class="btn btn-primary text-white btn-block rounded-0">정보 수정</a>
                            <a href="${root }/auth?action=member-delete&mno=${loginUser.mno}&email=${loginUser.email}" onclick="return confirm('정말로 탈퇴하시겠습니까?');" class="btn btn-danger text-white btn-block rounded-0">회원 탈퇴</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- JS 스크립트 -->
    <script src="${pageContext.request.contextPath}/assets/js/jquery-3.3.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/jquery-migrate-3.0.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/jquery-ui.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/owl.carousel.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/jquery.magnific-popup.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/aos.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>

</body>

</html>
