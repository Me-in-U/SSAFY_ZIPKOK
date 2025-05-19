<!-- src/components/RegisterForm.vue -->
<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-50">
    <div class="relative max-w-md w-full space-y-8 bg-white p-8 rounded-lg shadow-md">
      <!-- 닫기 버튼 -->
      <button
        type="button"
        @click="close"
        class="absolute top-4 right-4 text-gray-400 hover:text-gray-600"
        aria-label="가입 폼 닫기"
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
      <h2 class="text-3xl font-extrabold text-center text-gray-900">회원가입</h2>

      <!-- 등록 폼 -->
      <form @submit.prevent="handleRegister" class="space-y-6">
        <!-- 이름 -->
        <div>
          <label for="name" class="block text-sm font-medium text-gray-700">이름</label>
          <input
            id="name"
            v-model="name"
            type="text"
            autocomplete="name"
            required
            class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-emerald-500 focus:border-emerald-500 sm:text-sm"
            placeholder="홍길동"
          />
        </div>

        <!-- 이메일 -->
        <div>
          <label for="email" class="block text-sm font-medium text-gray-700">이메일</label>
          <input
            id="email"
            v-model="email"
            type="email"
            autocomplete="email"
            required
            class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-emerald-500 focus:border-emerald-500 sm:text-sm"
            placeholder="name@example.com"
          />
        </div>

        <!-- 비밀번호 -->
        <div>
          <label for="password" class="block text-sm font-medium text-gray-700">비밀번호</label>
          <div class="mt-1 relative">
            <input
              id="password"
              v-model="password"
              :type="showPassword ? 'text' : 'password'"
              autocomplete="new-password"
              required
              class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-emerald-500 focus:border-emerald-500 sm:text-sm"
              placeholder="••••••••"
            />
            <button
              type="button"
              @click="showPassword = !showPassword"
              class="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-500"
            >
              <svg
                v-if="showPassword"
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
                stroke-width="2"
              >
                <path d="M9.88 9.88a3 3 0 1 0 4.24 4.24" />
                <line x1="2" y1="2" x2="22" y2="22" />
              </svg>
              <svg
                v-else
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
                stroke-width="2"
              >
                <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z" />
                <circle cx="12" cy="12" r="3" />
              </svg>
            </button>
          </div>
        </div>

        <!-- 에러 메시지 -->
        <div v-if="error" class="text-red-600 text-sm">
          {{ error }}
        </div>

        <!-- 회원가입 버튼 -->
        <button
          type="submit"
          :disabled="isLoading"
          class="group relative w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-emerald-600 hover:bg-emerald-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-500 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <span v-if="isLoading" class="absolute left-0 inset-y-0 flex items-center pl-3">
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
                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
              />
            </svg>
          </span>
          {{ isLoading ? '가입 중...' : '회원가입' }}
        </button>

        <!-- 로그인 이동 -->
        <div class="text-center">
          <p class="text-sm text-gray-600">
            이미 계정이 있나요?
            <button @click="goLogin" class="text-emerald-600 hover:underline">로그인</button>
          </p>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const emit = defineEmits(['register-success'])
const router = useRouter()
const name = ref('')
const email = ref('')
const password = ref('')
const showPassword = ref(false)
const isLoading = ref(false)
const error = ref('')

// 닫기
function close() {
  router.push('/')
}

// 로그인 페이지로 이동
function goLogin() {
  router.replace('/login')
}

// 회원가입 처리
async function handleRegister() {
  error.value = ''
  isLoading.value = true
  try {
    const res = await axios.post('https://api.ssafy.blog/api/v1/members/register', {
      name: name.value,
      email: email.value,
      password: password.value,
    })
    if (res.data.success) {
      emit('register-success')
    } else {
      error.value = res.data.message || '회원가입에 실패했습니다.'
    }
  } catch (e) {
    error.value = e.response?.data?.message || '오류가 발생했습니다.'
  } finally {
    isLoading.value = false
  }
}
</script>
