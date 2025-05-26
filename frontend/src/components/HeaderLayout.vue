<template>
  <header class="bg-white shadow-sm">
    <div class="container mx-auto px-4 py-3 flex items-center justify-between">
      <!-- 로고 -->
      <div class="flex items-center">
        <img src="/favicon-96x96.png" alt="Logo" class="mt-1 w-[2.125rem] h-8 mr-2" />
        <router-link
          to="/"
          class="text-xl font-bold text-gray-800"
          title="홈으로 이동"
          aria-label="홈으로 이동"
          >집콕(ZIPKOK)</router-link
        >
      </div>
      <!-- 네비게이션 -->
      <nav class="hidden md:flex items-center space-x-6">
        <router-link
          to="/community"
          class="text-gray-600 hover:text-emerald-600 font-medium"
          title="커뮤니티"
          aria-label="커뮤니티로 이동"
          >커뮤니티</router-link
        >
        <button
          @click.prevent="goRecommended"
          class="text-gray-600 hover:text-emerald-600 font-medium"
          title="추천 매물"
          aria-label="추천 매물 표시"
        >
          추천 매물
        </button>

        <template v-if="!userStore.isLogged">
          <router-link
            to="/login"
            class="text-gray-600 hover:text-emerald-600 font-medium"
            title="로그인"
            aria-label="로그인 페이지로 이동"
            >로그인</router-link
          >
          <router-link
            to="/register"
            class="text-gray-600 hover:text-emerald-600 font-medium"
            title="회원가입"
            aria-label="회원가입 페이지로 이동"
            >회원가입</router-link
          >
        </template>
        <template v-else>
          <span class="text-gray-800 font-medium">{{ userStore.profile?.name }}님</span>
          <router-link to="/mypage" class="text-gray-600 hover:text-emerald-600 font-medium"
            >마이페이지</router-link
          >
          <button
            @click="emitLogout"
            class="text-gray-600 hover:text-emerald-600 font-medium"
            title="로그아웃"
            aria-label="로그아웃 버튼"
          >
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
        <router-link
          to="/community"
          class="block py-2 text-gray-600 hover:text-emerald-600 font-medium"
          title="커뮤니티"
          aria-label="커뮤니티로 이동"
          >커뮤니티</router-link
        >
        <button
          @click.prevent="goRecommended"
          class="block py-2 text-gray-600 hover:text-emerald-600 font-medium"
          title="추천 매물"
          aria-label="추천 매물 표시"
        >
          추천 매물
        </button>

        <template v-if="!userStore.isLogged">
          <router-link
            to="/login"
            class="block py-2 text-gray-600 hover:text-emerald-600 font-medium"
            title="로그인"
            aria-label="로그인 페이지로 이동"
            >로그인</router-link
          >
          <router-link
            to="/register"
            class="block py-2 text-gray-600 hover:text-emerald-600 font-medium"
            title="회원가입"
            aria-label="회원가입 페이지로 이동"
            >회원가입</router-link
          >
        </template>
        <template v-else>
          <router-link
            to="/mypage"
            class="block py-2 text-gray-600 hover:text-emerald-600 font-medium"
            title="마이페이지"
            aria-label="마이페이지로 이동"
            >마이페이지</router-link
          >
          <button
            @click="emitLogout"
            class="block py-2 text-gray-600 hover:text-emerald-600 font-medium"
            title="로그아웃"
            aria-label="로그아웃 버튼"
          >
            로그아웃
          </button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

// props
const { userStore } = defineProps({
  userStore: { type: Object, required: true },
})

// emit
const emit = defineEmits(['logout'])
function emitLogout() {
  emit('logout')
}

// 반응형 상태
const mobileMenuOpen = ref(false)

// 라우터
const router = useRouter()

function goRecommended() {
  router.push('/recommended')
}
</script>
