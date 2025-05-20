<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 헤더 -->
    <header class="bg-white shadow-sm">
      <div class="container mx-auto px-4 py-3 flex items-center justify-between">
        <!-- 로고 -->
        <div class="flex items-center">
          <img src="/favicon.ico" alt="Logo" class="w-6 h-6 mr-2" />
          <router-link to="/" class="text-xl font-bold text-gray-800">집콕(ZIPKOK)</router-link>
        </div>
        <!-- 네비게이션 -->
        <nav class="hidden md:flex items-center space-x-6">
          <a href="#" class="text-gray-600 hover:text-emerald-600 font-medium">투자 분석</a>
          <button
            @click.prevent="$router.push('/recommended')"
            class="text-gray-600 hover:text-emerald-600 font-medium"
          >
            추천 매물
          </button>
          <a href="#" class="text-gray-600 hover:text-emerald-600 font-medium">시장 동향</a>

          <template v-if="!userStore.isLogged">
            <router-link to="/login" class="text-gray-600 hover:text-emerald-600 font-medium"
              >로그인</router-link
            >
            <router-link to="/register" class="text-gray-600 hover:text-emerald-600 font-medium"
              >회원가입</router-link
            >
          </template>
          <template v-else>
            <span class="text-gray-800 font-medium">{{ userStore.profile?.name }}님</span>
            <router-link to="/" class="text-gray-600 hover:text-emerald-600 font-medium"
              >마이페이지</router-link
            >
            <button @click="handleLogout" class="text-gray-600 hover:text-emerald-600 font-medium">
              로그아웃
            </button>
          </template>
        </nav>

        <!-- 모바일 메뉴 토글 -->
        <button class="md:hidden" @click="mobileMenuOpen = !mobileMenuOpen">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <line x1="4" y1="6" x2="20" y2="6" />
            <line x1="4" y1="12" x2="20" y2="12" />
            <line x1="4" y1="18" x2="20" y2="18" />
          </svg>
        </button>
      </div>
      <!-- 모바일 메뉴 -->
      <div v-if="mobileMenuOpen" class="md:hidden bg-white border-t">
        <div class="container mx-auto px-4 py-2 space-y-2">
          <a href="#" class="block py-2 text-gray-600 hover:text-emerald-600 font-medium"
            >투자 분석</a
          >
          <button
            @click.prevent="$router.push('/recommended')"
            class="block py-2 text-gray-600 hover:text-emerald-600 font-medium"
          >
            추천 매물
          </button>
          <a href="#" class="block py-2 text-gray-600 hover:text-emerald-600 font-medium"
            >시장 동향</a
          >
          <template v-if="!userStore.isLogged">
            <router-link
              to="/login"
              class="block py-2 text-gray-600 hover:text-emerald-600 font-medium"
              >로그인</router-link
            >
            <router-link
              to="/register"
              class="block py-2 text-gray-600 hover:text-emerald-600 font-medium"
              >회원가입</router-link
            >
          </template>
          <template v-else>
            <router-link
              to="/mypage"
              class="block py-2 text-gray-600 hover:text-emerald-600 font-medium"
              >마이페이지</router-link
            >
            <button
              @click="handleLogout"
              class="block py-2 text-gray-600 hover:text-emerald-600 font-medium"
            >
              로그아웃
            </button>
          </template>
        </div>
      </div>
    </header>

    <!-- 기본 콘텐츠 -->
    <router-view />

    <!-- 로그인/회원가입 모달 -->
    <teleport to="body">
      <router-view name="modal" v-slot="{ Component }">
        <transition name="fade">
          <div
            v-if="Component"
            class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
          >
            <!-- 모달 컴포넌트에 이벤트 바인딩 -->
            <component
              :is="Component"
              @login-success="onLoginSuccess"
              @register-success="handleRegisterSuccess"
              @close="router.push('/')"
            />
          </div>
        </transition>
      </router-view>
    </teleport>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

// refs
const mobileMenuOpen = ref(false)

// router
const router = useRouter()

// useUserStore
const userStore = useUserStore()

// onMounted
onMounted(() => init())

// 앱 로드 시 사용자와 즐겨찾기 복원
async function init() {
  await userStore.fetchUser()
  if (userStore.isLogged) {
    await userStore.loadFavorites()
  }
}

// 로그인 성공 핸들러: fetchUser → loadFavorites → 모달 닫기
async function onLoginSuccess() {
  await init()
  router.push('/')
}

// 로그아웃
function handleLogout() {
  userStore.clearUser()
  router.push('/')
}

// 회원가입 성공 핸들러
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
