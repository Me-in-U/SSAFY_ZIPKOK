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
        ✕
      </button>

      <!-- 헤더 -->
      <h2 class="text-3xl font-extrabold text-center text-gray-900">회원가입</h2>

      <!-- 폼 -->
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
            class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-emerald-500 focus:border-emerald-500 sm:text-sm"
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
            class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-emerald-500 focus:border-emerald-500 sm:text-sm"
            placeholder="email@example.com"
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
              @focus="passwordFocused = true"
              @blur="passwordFocused = false"
              class="mt-1 block w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none sm:text-sm"
              :class="[
                passwordFocused ? 'focus:ring-emerald-500 focus:border-emerald-500' : '',
                passwordFocused && !allValid
                  ? 'border-red-600 focus:border-red-600 focus:ring-red-600'
                  : 'border-gray-300',
              ]"
              placeholder="••••••••"
            />
            <button
              type="button"
              @click="showPassword = !showPassword"
              class="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-500"
            >
              {{ showPassword ? '숨기기' : '보기' }}
            </button>
          </div>
        </div>

        <!-- 비밀번호 유효성 (password 입력 후에만 표시) -->
        <ul v-if="password.length > 0" class="text-sm space-y-1">
          <li
            :class="
              validLength ? 'text-emerald-600' : passwordFocused ? 'text-red-600' : 'text-gray-700'
            "
          >
            • 8자 이상, 25자 이하
          </li>
          <li
            :class="
              hasLetter ? 'text-emerald-600' : passwordFocused ? 'text-red-600' : 'text-gray-700'
            "
          >
            • 최소 하나의 문자 포함
          </li>
          <li
            :class="
              hasNumber ? 'text-emerald-600' : passwordFocused ? 'text-red-600' : 'text-gray-700'
            "
          >
            • 최소 하나의 숫자 포함
          </li>
          <li
            :class="
              hasSpecial ? 'text-emerald-600' : passwordFocused ? 'text-red-600' : 'text-gray-700'
            "
          >
            • 최소 하나의 특수문자 포함 (!"#$%&amp;'()*+,-./:;&lt;=&gt;?@[\]^_`{|}~)
          </li>
          <li
            :class="
              noInvalidChars
                ? 'text-emerald-600'
                : passwordFocused
                  ? 'text-red-600'
                  : 'text-gray-700'
            "
          >
            • 영문·숫자·허용 특수문자 외 불가 문자/공백 없음
          </li>
        </ul>

        <!-- 비밀번호 확인 -->
        <div>
          <label for="confirm" class="block text-sm font-medium text-gray-700">비밀번호 확인</label>
          <div class="mt-1 relative">
            <input
              id="confirm"
              v-model="confirmPassword"
              :type="showPassword ? 'text' : 'password'"
              autocomplete="new-password"
              required
              @focus="confirmFocused = true"
              @blur="confirmFocused = false"
              class="mt-1 block w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none sm:text-sm"
              :class="[
                confirmFocused
                  ? confirmMatch
                    ? 'focus:ring-emerald-500 focus:border-emerald-500'
                    : 'border-red-600 focus:border-red-600 focus:ring-red-600'
                  : 'border-gray-300',
              ]"
              placeholder="••••••••"
            />
          </div>
          <p class="mt-1 text-sm" :class="confirmMatch ? 'text-emerald-600' : 'text-red-600'">
            {{
              confirmPassword
                ? confirmMatch
                  ? '비밀번호가 일치합니다.'
                  : '비밀번호가 일치하지 않습니다.'
                : ''
            }}
          </p>
        </div>

        <!-- 에러 메시지 -->
        <div v-if="error" class="text-red-600 text-sm">
          {{ error }}
        </div>

        <!-- 회원가입 버튼 -->
        <button
          type="submit"
          :disabled="!allValid || !confirmMatch || isLoading"
          class="w-full flex justify-center py-2 px-4 rounded-md text-sm font-medium text-white"
          :class="
            allValid && confirmMatch
              ? 'bg-emerald-600 hover:bg-emerald-700 focus:ring-emerald-500'
              : 'bg-gray-400 cursor-not-allowed'
          "
        >
          <span v-if="isLoading" class="mr-2">⏳</span>
          {{ isLoading ? '가입 중...' : '회원가입' }}
        </button>

        <!-- 로그인 이동 -->
        <div class="text-center">
          <p class="text-sm text-gray-600">
            이미 계정이 있나요?
            <button type="button" @click="goLogin" class="text-emerald-600 hover:underline">
              로그인
            </button>
          </p>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const emit = defineEmits(['register-success'])
const router = useRouter()

const name = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const confirmFocused = ref(false)
const showPassword = ref(false)
const passwordFocused = ref(false)
const isLoading = ref(false)
const error = ref('')

// 비밀번호 유효성 검사
const validLength = computed(() => password.value.length >= 8 && password.value.length <= 25)
const hasLetter = computed(() => /[A-Za-z]/.test(password.value))
const hasNumber = computed(() => /\d/.test(password.value))
const hasSpecial = computed(() => /[!"#$%&'()*+,\-./:;<=>?@[\\\]^_`{|}~]/.test(password.value))
const noInvalidChars = computed(() =>
  /^[A-Za-z0-9!"#$%&'()*+,\-./:;<=>?@[\\\]^_`{|}~]+$/.test(password.value),
)
const allValid = computed(
  () =>
    validLength.value &&
    hasLetter.value &&
    hasNumber.value &&
    hasSpecial.value &&
    noInvalidChars.value,
)

// 비밀번호 확인 일치 여부
const confirmMatch = computed(
  () => confirmPassword.value !== '' && password.value === confirmPassword.value,
)

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
  if (!allValid.value || !confirmMatch.value) return
  error.value = ''
  isLoading.value = true
  try {
    const res = await axios.post('https://api.ssafy.blog/api/v1/members/regist', {
      name: name.value,
      email: email.value,
      password: password.value,
    })
    console.log(res)
    if (res.data.status === 'SUCCESS') {
      emit('register-success')
      close()
    } else {
      error.value = res.data.message || '회원가입에 실패했습니다.'
    }
  } catch (e) {
    const status = e.response?.status
    if (status === 409) {
      // 백엔드에서 중복 키 위반 시 409로 내려온다고 가정
      error.value = '이미 존재하는 이메일입니다.'
    } else if (status >= 500) {
      error.value = '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.'
    } else {
      // 400번대 기타 클라이언트 오류
      error.value = e.response?.data?.message || '회원가입에 실패했습니다.'
    }
  } finally {
    isLoading.value = false
  }
}
</script>
