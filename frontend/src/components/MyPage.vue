<!-- MyPage.vue -->
<template>
  <transition name="fade">
    <div
      v-if="visible"
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-50"
    >
      <div
        class="bg-white rounded-xl shadow-2xl w-full max-w-5xl max-h-[90vh] overflow-auto relative"
      >
        <!-- 헤더 -->
        <div class="flex items-center justify-between px-6 py-4 border-b">
          <h2 class="text-2xl font-bold text-gray-800">마이페이지</h2>
          <button @click="close" class="text-gray-400 hover:text-gray-700 text-xl">✕</button>
        </div>

        <!-- 탭 버튼 -->
        <div class="px-6 pt-4 flex space-x-4 border-b bg-gray-50">
          <button
            @click="activeTab = 'info'"
            :class="activeTab === 'info' ? 'bg-emerald-600 text-white' : 'bg-white text-gray-700'"
            class="px-4 py-2 rounded-lg shadow"
          >
            회원 정보 수정
          </button>
          <button
            @click="activeTab = 'favorites'"
            :class="
              activeTab === 'favorites' ? 'bg-emerald-600 text-white' : 'bg-white text-gray-700'
            "
            class="px-4 py-2 rounded-lg shadow"
          >
            내 즐겨찾기
          </button>
        </div>

        <!-- 컨텐츠 -->
        <div class="px-6 py-6 bg-gray-50">
          <!-- 회원 정보 수정 -->
          <div v-if="activeTab === 'info'">
            <form @submit.prevent="updateProfile" class="space-y-4 max-w-md mx-auto">
              <div>
                <label class="block text-sm font-medium text-gray-700">이름</label>
                <input
                  v-model="form.name"
                  type="text"
                  class="mt-1 block w-full border rounded p-2"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">이메일</label>
                <input
                  v-model="form.email"
                  type="email"
                  class="mt-1 block w-full border rounded p-2"
                  disabled
                />
              </div>
              <!-- MyPage.vue, 이름 입력 밑에 -->
              <div>
                <label for="currentPassword" class="block text-sm font-medium text-gray-700"
                  >현재 비밀번호</label
                >
                <div class="mt-1 relative">
                  <input
                    :type="showPassword1 ? 'text' : 'password'"
                    id="currentPassword"
                    v-model="form.currentPassword"
                    required
                    class="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-emerald-500 focus:border-emerald-500 sm:text-sm"
                    placeholder="현재 비밀번호 입력"
                  />
                  <button
                    type="button"
                    class="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-500"
                    @click="showPassword1 = !showPassword1"
                  >
                    <svg
                      v-if="showPassword1"
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
                <label for="newPassword" class="block text-sm font-medium text-gray-700"
                  >새 비밀번호</label
                >
                <div class="mt-1 relative">
                  <input
                    :type="showPassword2 ? 'text' : 'password'"
                    id="newPassword"
                    v-model="form.newPassword"
                    required
                    class="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-emerald-500 focus:border-emerald-500 sm:text-sm"
                    placeholder="변경할 비밀번호 입력"
                  />
                  <button
                    type="button"
                    class="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-500"
                    @click="showPassword2 = !showPassword2"
                  >
                    <svg
                      v-if="showPassword2"
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

              <div class="flex justify-end">
                <button
                  type="submit"
                  class="bg-emerald-600 text-white px-4 py-2 rounded hover:bg-emerald-700"
                >
                  저장
                </button>
              </div>
            </form>
          </div>

          <!-- 내 즐겨찾기 -->
          <div v-else>
            <div v-if="loading" class="flex items-center justify-center h-64">
              <svg
                class="animate-spin h-10 w-10 text-emerald-600"
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
                  d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"
                />
              </svg>
            </div>
            <div v-else-if="!favorites.length" class="text-center text-gray-500 py-16">
              아직 즐겨찾기한 매물이 없습니다.
            </div>
            <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
              <div
                v-for="item in favorites"
                :key="item.aptSeq"
                class="bg-white rounded-lg overflow-hidden shadow hover:shadow-lg transition flex flex-col"
              >
                <div class="h-48 bg-gray-50 flex items-center justify-center">
                  <img
                    :src="item.imgPath || catPlaceholder"
                    alt="아파트"
                    class="w-full h-full"
                    :class="item.imgPath ? 'object-cover' : 'object-contain p-4'"
                  />
                </div>
                <div class="p-4 flex-1 flex flex-col">
                  <h3 class="text-lg font-semibold text-gray-800 mb-1">{{ item.listingName }}</h3>
                  <p class="text-emerald-600 font-bold mb-2">{{ formatCurrency(item.price) }}원</p>
                  <p class="text-sm text-gray-500 mb-2">{{ item.spec || '스펙 정보 없음' }}</p>
                  <p class="text-sm text-gray-600 line-clamp-3 mb-4">
                    {{ item.description || '설명 없음' }}
                  </p>
                  <button
                    @click="goSidebar(item.aptSeq)"
                    class="mt-auto bg-emerald-600 text-white py-2 rounded-md hover:bg-emerald-700 transition"
                  >
                    해당 아파트로 이동
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import axios from 'axios'
import catPlaceholder from '@/assets/cat-placeholder.png'

// router
const router = useRouter()

// store
const userStore = useUserStore()

// ref
const showPassword1 = ref(false)
const showPassword2 = ref(false)
const visible = ref(true)
const loading = ref(false)
const activeTab = ref('favorites')

// reactive
const form = reactive({
  name: '',
  email: '',
  currentPassword: '', // ← 추가
  newPassword: '', // ← 추가
})

const favorites = ref([])

onMounted(async () => {
  // 프로필
  if (!userStore.profile?.mno) await userStore.fetchUser()
  form.name = userStore.profile.name
  form.email = userStore.profile.email

  loading.value = true
  try {
    // 프로필 보장
    if (!userStore.profile?.mno) await userStore.fetchUser()
    const mno = userStore.profile.mno
    // 백엔드에서 HouseInfo 리스트 가져오기
    const res = await axios.get(`https://api.ssafy.blog/api/v1/members/${mno}/favorites`)
    console.log('favorites API response:', res.data)
    favorites.value = res.data.data.result
    console.log('favorites.value set:', favorites.value)
  } catch (e) {
    console.error('즐겨찾기 목록 로드 실패', e)
  } finally {
    loading.value = false
  }
})

async function updateProfile() {
  try {
    const payload = {
      mno: userStore.profile.mno,
      name: form.name,
      currentPassword: form.currentPassword,
      newPassword: form.newPassword, // ← 이 필드에 제대로 값이 실립니다
    }
    await axios.put('https://api.ssafy.blog/api/v1/members', payload)
    alert('회원 정보가 수정되었습니다.')
    userStore.profile.name = form.name
    // 비밀번호 필드 초기화
    form.currentPassword = ''
    form.newPassword = ''
    close()
  } catch (e) {
    console.error('회원 정보 수정 실패', e)
    alert(e.response?.data?.message || '수정 중 오류가 발생했습니다.')
  }
}

function close() {
  router.back()
}

function goSidebar(aptSeq) {
  // 모달 닫기
  close()
  // 메인 페이지로 이동하면서 detail 쿼리 전달
  router.push({ path: '/', query: { detail: aptSeq } })
}

function formatCurrency(val) {
  return val != null ? val.toLocaleString() : '-'
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
