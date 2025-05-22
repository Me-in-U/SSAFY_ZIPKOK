<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-50">
    <div class="relative max-w-md w-full bg-white p-8 rounded-lg shadow-md">
      <!-- 닫기 버튼 -->
      <button
        type="button"
        @click="close"
        class="absolute top-4 right-4 text-gray-400 hover:text-gray-600"
        aria-label="닫기"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          class="h-6 w-6"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          stroke-width="2"
        >
          <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>

      <!-- 헤더 -->
      <div class="text-center mb-6">
        <img src="/favicon.ico" alt="Logo" class="mx-auto w-8 h-8" />
        <h2 class="mt-4 text-3xl font-extrabold text-gray-900">부동산 투자 AI</h2>
        <p class="mt-2 text-sm text-gray-600">
          계정에 로그인하고<br />맞춤형 부동산 투자 정보를 받아보세요
        </p>
      </div>

      <!-- 에러 메시지 -->
      <div
        v-if="error"
        class="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-md mb-4 text-sm"
      >
        {{ error }}
      </div>

      <!-- 로그인 폼 -->
      <form class="space-y-6" @submit.prevent="handleLogin">
        <div>
          <label for="email" class="block text-sm font-medium text-gray-700">이메일</label>
          <input
            id="email"
            v-model="email"
            type="email"
            required
            class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-emerald-500 focus:border-emerald-500 sm:text-sm"
            placeholder="name@example.com"
          />
        </div>

        <div>
          <label for="password" class="block text-sm font-medium text-gray-700">비밀번호</label>
          <div class="mt-1 relative">
            <input
              :type="showPassword ? 'text' : 'password'"
              id="password"
              v-model="password"
              required
              class="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-emerald-500 focus:border-emerald-500 sm:text-sm"
              placeholder="••••••••"
            />
            <button
              type="button"
              class="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-500"
              @click="showPassword = !showPassword"
            >
              <svg
                v-if="showPassword"
                xmlns="http://www.w3.org/2000/svg"
                width="18"
                height="18"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path d="M9.88 9.88a3 3 0 1 0 4.24 4.24" />
                <path
                  d="M10.73 5.08A10.43 10.43 0 0 1 12 5c7 0 10 7 10 7a13.16
                         13.16 0 0 1-1.67 2.68"
                />
                <path
                  d="M6.61 6.61A13.526 13.526 0 0 0 2 12s3 7 10 7a9.74 9.74
                         0 0 0 5.39-1.61"
                />
                <line x1="2" y1="2" x2="22" y2="22" />
              </svg>
              <svg
                v-else
                xmlns="http://www.w3.org/2000/svg"
                width="18"
                height="18"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z" />
                <circle cx="12" cy="12" r="3" />
              </svg>
            </button>
          </div>
        </div>

        <div>
          <button
            type="submit"
            :disabled="isLoading"
            class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-emerald-600 hover:bg-emerald-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-500 disabled:opacity-50 disabled:cursor-not-allowed relative"
          >
            <span v-if="isLoading" class="absolute inset-y-0 left-0 pl-3 flex items-center">
              <svg
                class="animate-spin h-5 w-5 text-white"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
              >
                <circle
                  class="opacity-25"
                  cx="12"
                  cy="12"
                  r="10"
                  stroke="currentColor"
                  stroke-width="4"
                />
                <path
                  class="opacity-75"
                  fill="currentColor"
                  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2
                         5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824
                         3 7.938l3-2.647z"
                />
              </svg>
            </span>
            {{ isLoading ? '로그인 중...' : '로그인' }}
          </button>
        </div>

        <div>
          <button
            type="button"
            @click="goRegister"
            class="w-full flex justify-center py-2 px-4 border border-emerald-600 rounded-md shadow-sm text-sm font-medium text-emerald-600 hover:bg-emerald-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-500"
          >
            회원가입
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { useUserStore } from '@/stores/user'

// emit props
const emit = defineEmits(['login-success'])

// refs
const email = ref('')
const password = ref('')
const showPassword = ref(false)
const isLoading = ref(false)
const error = ref('')

// router
const router = useRouter()

// user store
const userStore = useUserStore()
// 로그인 처리
async function handleLogin() {
  error.value = ''
  isLoading.value = true
  try {
    const res = await axios.post('https://api.ssafy.blog/api/v1/members/login', {
      email: email.value,
      password: password.value,
    })
    if (res.data.status === 'SUCCESS') {
      const { token, user } = res.data.data
      userStore.setUser({ user, token })
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
      await userStore.loadFavorites()
      emit('login-success')
      console.log('[로그인 성공]:', res.data)
      router.back()
      close()
    } else {
      error.value = res.data.message || '로그인에 실패했습니다.'
    }
  } catch (e) {
    console.error(e)
    error.value = e.response?.data?.error || '이메일 또는 비밀번호가 올바르지 않습니다.'
  } finally {
    isLoading.value = false
  }
}

// 닫기
function close() {
  router.push('/')
}

// 회원가입 페이지로 이동
function goRegister() {
  router.replace('/register')
}
</script>
