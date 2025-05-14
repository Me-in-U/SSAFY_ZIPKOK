<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<link rel="stylesheet" href="/css/bootstrap.min.css">
<link rel="stylesheet" href="/css/style.css">

<!-- Navbar -->
<div class="site-navbar mt-4">
  <div class="container py-1">
    <div class="row align-items-center">
      <div class="col-8 col-md-8 col-lg-4">
        <h1 class="mb-0">
          <a href="/" class="text-white h2 mb-0">
            <strong>집콕<span class="text-danger">.</span></strong>
          </a>
        </h1>
      </div>
      <div class="col-4 col-md-4 col-lg-8">
        <nav class="site-navigation text-right text-md-right" role="navigation">
          <div class="d-inline-block d-lg-none ml-md-0 mr-auto py-3">
            <a href="#" class="site-menu-toggle js-menu-toggle text-white">
              <span class="icon-menu h3"></span>
            </a>
          </div>

          <ul id="navbarMenu" class="site-menu js-clone-nav d-none d-lg-block">
            <!-- JS가 여기에 <li> 항목을 채워넣습니다 -->
          </ul>
        </nav>
      </div>
    </div>
  </div>
</div>

<script>
(async function() {
  const menu = document.getElementById('navbarMenu');
  menu.innerHTML = ''; // 1) 기존 내용 비우기

  // 2) helper: li > a 생성 함수
  function addItem(text, href, id) {
    const li = document.createElement('li');
    const a  = document.createElement('a');
    a.textContent = text;
    console.log('메뉴 항목:', text);
    if (href) a.href = href;
    if (id)   a.id   = id;
    li.appendChild(a);
    menu.appendChild(li);
  }

  // 3) 공통 메뉴
  addItem('메인화면', '/');
  addItem('마이페이지', '/member/mypage');

  const token = localStorage.getItem('jwtToken');
  if (!token) {
    // 비로그인
    addItem('회원가입', '/member/mvRegist');
    addItem('로그인',   '/member/mvLogin');
  } else {
    try {
      const res = await fetch('/api/v1/members/me', {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + token
        }
      });
      if (!res.ok) throw new Error('인증 실패');

      const json = await res.json();
      const name = json.user.name; 
      console.log('로그인 사용자 정보:', json);
      console.log('로그인 사용자 이름:', name);
      // 사용자 이름
      addItem(name + "님 접속 중");
      // 로그아웃
      addItem('로그아웃', '#', 'logoutBtn');
    } catch (e) {
      console.warn('토큰 검증 오류, 로그아웃 처리:', e);
      localStorage.removeItem('jwtToken');
      addItem('회원가입', '/member/mvRegist');
      addItem('로그인',   '/member/mvLogin');
    }
  }

  // 4) 로그아웃 핸들러
  const logoutBtn = document.getElementById('logoutBtn');
  if (logoutBtn) {
    logoutBtn.addEventListener('click', e => {
      e.preventDefault();
      localStorage.removeItem('jwtToken');
      window.location.href = '/';
    });
  }
})();
</script>
