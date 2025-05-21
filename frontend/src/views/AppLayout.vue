<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 뉴스 티커 -->
    <NewsTicker />

    <!-- 헤더 -->
    <HeaderLayout
      :user-store="userStore"
      @logout="handleLogout"
      @login-success="onLoginSuccess"
      @register-success="handleRegisterSuccess"
    />

    <!-- 기본 콘텐츠 -->
    <router-view />

    <!-- 로그인/회원가입 모달 -->
    <LoginRegisterModal @login-success="onLoginSuccess" @register-success="handleRegisterSuccess" />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import HeaderLayout from '@/components/HeaderLayout.vue'
import NewsTicker from '@/components/NewsTicker.vue'
import LoginRegisterModal from '@/components/LoginRegisterModal.vue'

// 스토어, 라우터
const router = useRouter()
const userStore = useUserStore()

// 컴포넌트 마운트
onMounted(async () => {
  await userStore.fetchUser()
  if (userStore.isLogged) await userStore.loadFavorites()
})

// 이벤트 핸들러
function onLoginSuccess() {
  router.push('/')
}

function handleLogout() {
  userStore.clearUser()
  router.push('/')
}

function handleRegisterSuccess() {
  router.push('/login')
}
</script>

<style>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
