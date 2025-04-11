<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="root" value="${pageContext.servletContext.contextPath }" />
<!DOCTYPE html>
<html lang="ko">
<head>
<title>SSAFY Home Web</title>
<meta charset="UTF-8">
<meta name="viewport"
    content="width=device-width, initial-scale=1, shrink-to-fit=no">

<link rel="stylesheet"
    href="https://fonts.googleapis.com/css?family=Nunito+Sans:200,300,400,700,900|Roboto+Mono:300,400,500">
<link rel="stylesheet" href="${root}/assets/fonts/icomoon/style.css">
<link rel="stylesheet" href="${root}/assets/css/bootstrap.min.css">
<link rel="stylesheet" href="${root}/assets/css/magnific-popup.css">
<link rel="stylesheet" href="${root}/assets/css/jquery-ui.css">
<link rel="stylesheet" href="${root}/assets/css/owl.carousel.min.css">
<link rel="stylesheet"
    href="${root}/assets/css/owl.theme.default.min.css">
<link rel="stylesheet"
    href="${root}/assets/css/bootstrap-datepicker.css">
<link rel="stylesheet"
    href="${root}/assets/css/mediaelementplayer.css">
<link rel="stylesheet"
    href="${root}/assets/css/animate.css">
<link rel="stylesheet"
    href="${root}/assets/fonts/flaticon/font/flaticon.css">
<link rel="stylesheet"
    href="${root}/assets/css/fl-bigmug-line.css">
<link rel="stylesheet"
    href="${root}/assets/css/aos.css">
<link rel="stylesheet"
    href="${root}/assets/css/style.css">
</head>

<body>
    <div class="site-loader"></div>
    <div class="site-wrap">
        <!-- Mobile Menu -->
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

    <!-- 슬라이더 -->
    <div class="slide-one-item home-slider owl-carousel">
        <div class="site-blocks-cover overlay"
            style="background-image: url(${root}/assets/img/hero_bg_1.jpg);"
            data-aos="fade" data-stellar-background-ratio="0.5">
            <div class="container">
                <div
                    class="row align-items-center justify-content-center text-center">
                    <div class="col-md-10">
                        <span
                            class="d-inline-block bg-success text-white px-3 mb-3 property-offer-type rounded">매매</span>
                        <h1 class="mb-2">반포 자이아파트</h1>
                        <p class="mb-5">
                            <strong class="h2 text-success font-weight-bold">30억</strong>
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
        <div class="site-blocks-cover overlay"
            style="background-image: url(${root}/assets/img/hero_bg_2.jpg);"
            data-aos="fade" data-stellar-background-ratio="0.5">
            <div class="container">
                <div
                    class="row align-items-center justify-content-center text-center">
                    <div class="col-md-10">
                        <span
                            class="d-inline-block bg-danger text-white px-3 mb-3 property-offer-type rounded">전세</span>
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
                    <h3
                        class="d-inline-block text-white px-3 mb-3 property-offer-type rounded fs-4">법정동
                        기반의 서비스(HappyHouse):</h3>
                    <div class="row align-items-end">
                        <div class="col-md-3">
                            <label for="list-types">시도 선택: </label>
                            <div class="select-wrap">
                                <span class="icon icon-arrow_drop_down"></span> <select
                                    id="sido" name="list-types"
                                    class="form-control d-block rounded-0">
                                    <option value="" selected disabled>시도 선택</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <label for="offer-types">구군 선택: </label>
                            <div class="select-wrap">
                                <span class="icon icon-arrow_drop_down"></span> <select
                                    id="gugun" name="offer-types"
                                    class="form-control d-block rounded-0">
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
    <script
        src="${root}/assets/js/jquery-3.3.1.min.js"></script>
    <script
        src="${root}/assets/js/jquery-migrate-3.0.1.min.js"></script>
    <script src="${root}/assets/js/jquery-ui.js"></script>
    <script
        src="${root}/assets/js/bootstrap.min.js"></script>
    <script
        src="${root}/assets/js/owl.carousel.min.js"></script>
    <script
        src="${root}/assets/js/jquery.magnific-popup.min.js"></script>
    <script
        src="https://sgisapi.kostat.go.kr/OpenAPI3/auth/javascriptAuth?consumer_key=46d0d3813d9c4bc8af25"></script>
    <script src="${root}/assets/js/aos.js"></script>
    <script src="${root}/assets/js/main.js"></script>
    <script src="${root}/assets/js/keys.js"></script>
    <script src="${root}/assets/js/common.js"></script>
    <script src="${root}/assets/js/kostat.js"></script>
    <script
        src="${root}/assets/js/happyHouse.js"></script>

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
  <script type="text/javascript">
        const alertMsg = `${param.alertMsg}` || `${alertMsg}`; // 메시지 자체에 ""가 포함 되기도 함
        console.log(alertMsg)
        if (alertMsg) {
            alert(alertMsg);
        }
        /*   
         html에서 작성하고 주석 처리 해 두는게 오히려 가독성이 좋을 수도. 
         <c:remove var="alertMsg" scope="session"/>  
         */
    </script>
</body>
</html>