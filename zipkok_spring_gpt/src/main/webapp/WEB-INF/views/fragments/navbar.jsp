<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<link rel="stylesheet" href="/css/bootstrap.min.css">
<link rel="stylesheet" href="/css/style.css">
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
					<a href="/" class="text-white h2 mb-0"> <strong>집콕<span class="text-danger">.</span>
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
						<li class="active"><a href="/">메인화면</a></li>
						<li><a href="/member/mypage">마이페이지</a></li>
							<li>
								<c:choose>
									<c:when test="${empty userInfo}">
										<a href="/member/mvRegist">회원가입</a>
										<a href="/member/mvLogin">로그인</a>
									</c:when>
									<c:otherwise>
										<a>${userInfo.name} 님 접속 중</a>
									</c:otherwise>
								</c:choose>
							</li>
							<li>
								<c:if test="${!empty userInfo}">
									<a href="/member/logout">로그아웃</a>
								</c:if>
							</li>
					</ul>
				</nav>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
	const alertMsg = `${param.alertMsg}` || `${alertMsg}`; // 메시지 자체에 ""가 포함되기도 함
	if (alertMsg) {
		alert(alertMsg);
	}
	/*   
	<c:remove var="alertMsg" scope="session"/>  
	 */
</script>