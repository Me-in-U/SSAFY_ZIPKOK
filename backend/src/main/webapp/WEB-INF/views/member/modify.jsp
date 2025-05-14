<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="form" uri="http://java.sun.com/jsp/jstl/sql"%>
<!DOCTYPE html>
<html lang="ko">

<head>
    <title>회원정보 수정</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="stylesheet" href="/css/bootstrap.min.css">
    <link rel="stylesheet" href="/css/style.css">
</head>

<body>
	<div class="container mt-5">
		<h2 class="text-center mb-4">회원정보 수정</h2>
		<c:if test="${not empty alertMsg}">
			<div class="alert alert-warning">
				<strong>${alertMsg}</strong>
			</div>
		</c:if>

		<form action="/member/modify" method="post">
			<input type="hidden" name="mno" value="${loginUser.mno}" />
			<div class="form-group">
				<label for="name">이름</label> <input type="text" class="form-control"
					id="name" name="name" value="${loginUser.name}" required />
			</div>

			<div class="form-group">
				<label for="email">이메일</label> <input type="email"
					class="form-control" id="email" name="email" readonly
					value="${loginUser.email}" />
			</div>

			<div class="form-group">
				<label for="password">새로운 비밀번호</label> <input type="password"
					class="form-control" id="password" name="password" required />
			</div>

			<div class="form-group text-center">
				<button type="submit" class="btn btn-primary">수정하기</button>
				<a href="mypage.jsp" class="btn btn-secondary">취소</a>
			</div>
		</form>
	</div>

	<script src="/js/jquery-3.3.1.min.js"></script>
	<script src="/js/bootstrap.min.js"></script>
</body>
</html>
