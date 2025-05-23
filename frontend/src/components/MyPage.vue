<template>
  <transition name="fade">
    <div v-if="visible"
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-50 backdrop-blur-sm">
      <div class="bg-white rounded-xl shadow-2xl w-full max-w-5xl max-h-[90vh] overflow-hidden relative flex flex-col">
        <!-- 헤더 -->
        <div class="relative">
          <div class="absolute inset-0 bg-gradient-to-r from-emerald-600 to-emerald-400 opacity-90"></div>
          <div class="relative px-8 py-6 flex justify-between items-center">
            <div>
              <h2 class="text-3xl font-bold text-white">마이페이지</h2>
              <p class="text-emerald-50 mt-1">내 정보 관리 및 즐겨찾기 매물을 확인하세요</p>
            </div>
            <button @click="close"
              class="w-10 h-10 rounded-full bg-white bg-opacity-20 flex items-center justify-center text-white hover:bg-opacity-30 transition-all duration-200">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24"
                stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>

        <!-- 프로필 요약 -->
        <div class="bg-white px-8 py-4 border-b">
          <div class="flex items-center space-x-4">
            <div
              class="w-16 h-16 rounded-full bg-emerald-100 flex items-center justify-center text-emerald-600 text-2xl font-bold overflow-hidden">
              {{ userStore.profile?.name?.charAt(0) || '?' }}
            </div>
            <div>
              <h3 class="text-xl font-bold text-gray-800">{{ userStore.profile?.name || '사용자' }}님</h3>
              <p class="text-gray-500">{{ userStore.profile?.email || '이메일 정보 없음' }}</p>
            </div>
            <div class="ml-auto flex items-center space-x-2">
              <div class="text-center px-4 py-2 bg-gray-50 rounded-lg">
                <p class="text-sm text-gray-500">즐겨찾기</p>
                <p class="text-xl font-bold text-emerald-600">{{ favorites.length }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 탭 버튼 -->
        <div class="px-8 py-4 bg-white border-b">
          <div class="flex space-x-1 bg-gray-100 p-1 rounded-lg inline-flex">
            <button @click="activeTab = 'favorites'" :class="activeTab === 'favorites' ? 'bg-white text-emerald-600 shadow-sm' : 'bg-transparent text-gray-600 hover:text-gray-800'
              " class="px-4 py-2 rounded-md font-medium transition-all duration-200">
              즐겨찾기
            </button>
            <button @click="activeTab = 'info'" :class="activeTab === 'info' ? 'bg-white text-emerald-600 shadow-sm' : 'bg-transparent text-gray-600 hover:text-gray-800'
              " class="px-4 py-2 rounded-md font-medium transition-all duration-200">
              회원 정보 수정
            </button>
          </div>
        </div>

        <!-- 컨텐츠 -->
        <div class="flex-1 overflow-auto bg-gray-50">
          <!-- 회원 정보 수정 -->
          <div v-if="activeTab === 'info'" class="p-8">
            <div class="max-w-2xl mx-auto bg-white rounded-xl shadow-sm p-8">
              <h3 class="text-xl font-bold text-gray-800 mb-6">회원 정보 수정</h3>

              <form @submit.prevent="updateProfile" class="space-y-6">
                <div class="grid grid-cols-1 gap-6">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">이름</label>
                    <input v-model="form.name" type="text"
                      class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all duration-200"
                      placeholder="이름을 입력하세요" />
                  </div>

                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">이메일</label>
                    <input v-model="form.email" type="email"
                      class="w-full px-4 py-3 rounded-lg border border-gray-300 bg-gray-50 cursor-not-allowed"
                      disabled />
                    <p class="mt-1 text-xs text-gray-500">이메일은 변경할 수 없습니다</p>
                  </div>

                  <div>
                    <label for="currentPassword" class="block text-sm font-medium text-gray-700 mb-1">현재 비밀번호</label>
                    <div class="relative">
                      <input :type="showPassword1 ? 'text' : 'password'" id="currentPassword"
                        v-model="form.currentPassword"
                        class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all duration-200 pr-10"
                        placeholder="현재 비밀번호 입력" />
                      <button type="button"
                        class="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-600"
                        @click="showPassword1 = !showPassword1">
                        <svg v-if="showPassword1" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none"
                          viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />
                        </svg>
                        <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24"
                          stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.543 7-1.274 4.057-5.064 7-9.543 7-4.477 0-8.268-2.943-9.542-7z" />
                        </svg>
                      </button>
                    </div>
                  </div>

                  <div>
                    <label for="newPassword" class="block text-sm font-medium text-gray-700 mb-1">새 비밀번호</label>
                    <div class="relative">
                      <input :type="showPassword2 ? 'text' : 'password'" id="newPassword" v-model="form.newPassword"
                        class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all duration-200 pr-10"
                        placeholder="변경할 비밀번호 입력" />
                      <button type="button"
                        class="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-600"
                        @click="showPassword2 = !showPassword2">
                        <svg v-if="showPassword2" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none"
                          viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />
                        </svg>
                        <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24"
                          stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.543 7-1.274 4.057-5.064 7-9.543 7-4.477 0-8.268-2.943-9.542-7z" />
                        </svg>
                      </button>
                    </div>
                    <p class="mt-1 text-xs text-gray-500">8자 이상, 영문/숫자/특수문자 조합</p>
                  </div>
                </div>

                <div class="pt-4">
                  <button type="submit"
                    class="w-full py-3 px-4 bg-emerald-600 hover:bg-emerald-700 text-white font-medium rounded-lg shadow-sm transition-colors duration-200 flex items-center justify-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24"
                      stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M8 7H5a2 2 0 00-2 2v9a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-3m-1 4l-3 3m0 0l-3-3m3 3V4" />
                    </svg>
                    변경사항 저장
                  </button>
                </div>
              </form>
            </div>
          </div>

          <!-- 내 즐겨찾기 -->
          <div v-else class="p-8">
            <div v-if="loading" class="flex flex-col items-center justify-center h-64">
              <svg class="animate-spin h-12 w-12 text-emerald-600 mb-4" xmlns="http://www.w3.org/2000/svg" fill="none"
                viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z" />
              </svg>
              <p class="text-gray-500">즐겨찾기 매물을 불러오는 중입니다...</p>
            </div>

            <div v-else-if="!favorites.length" class="flex flex-col items-center justify-center py-16">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 text-gray-300 mb-4" fill="none"
                viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
              </svg>
              <h3 class="text-lg font-medium text-gray-700 mb-1">아직 즐겨찾기한 매물이 없습니다</h3>
              <p class="text-gray-500 text-center max-w-md mb-4">
                마음에 드는 매물을 즐겨찾기에 추가하고 쉽게 관리해보세요.
              </p>
              <button @click="close"
                class="px-4 py-2 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700 transition-colors shadow-sm">
                매물 둘러보기
              </button>
            </div>

            <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
              <div v-for="item in favorites" :key="item.aptSeq"
                class="group bg-white rounded-xl overflow-hidden shadow-md hover:shadow-xl transition-all duration-300 flex flex-col">
                <div class="relative h-48 overflow-hidden">
                  <img :src="item.imgPath || catPlaceholder" alt="아파트"
                    class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
                    :class="item.imgPath ? '' : 'object-contain p-4'" />
                  <div
                    class="absolute inset-0 bg-gradient-to-t from-black/50 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300">
                  </div>
                  <button
                    class="absolute top-3 right-3 w-8 h-8 rounded-full bg-white/80 flex items-center justify-center text-red-500 hover:bg-white transition-all duration-200"
                    @click.stop="removeFavorite(item.aptSeq)">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                      <path fill-rule="evenodd"
                        d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z"
                        clip-rule="evenodd" />
                    </svg>
                  </button>
                </div>

                <div class="p-5 flex-1 flex flex-col">
                  <div class="flex justify-between items-start mb-2">
                    <h3
                      class="text-lg font-semibold text-gray-800 group-hover:text-emerald-600 transition-colors duration-200">
                      {{ item.listingName || item.aptNm || '정보 없음' }}
                    </h3>
                    <span class="bg-emerald-100 text-emerald-800 text-xs px-2 py-1 rounded-full">
                      {{ item.propertyType || '정보 없음' }}
                    </span>
                  </div>

                  <p class="text-emerald-600 font-bold mb-2">{{ formatCurrency(item.price) }}</p>
                  <p class="text-sm text-gray-500 mb-3">{{ item.spec || '스펙 정보 없음' }}</p>
                  <p class="text-sm text-gray-600 line-clamp-3 mb-4">
                    {{ item.description || '설명 없음' }}
                  </p>

                  <div class="mt-auto pt-3 border-t border-gray-100 flex justify-between items-center">
                    <button @click="goSidebar(item.aptSeq)"
                      class="text-emerald-600 text-sm font-medium hover:text-emerald-700 transition-colors flex items-center">
                      지도로 이동
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 ml-1" fill="none" viewBox="0 0 24 24"
                        stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                      </svg>
                    </button>
                  </div>
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
  currentPassword: '',
  newPassword: '',
})

const favorites = ref([])

onMounted(async () => {
  // 프로필
  await userStore.fetchUser()
  form.name = userStore.profile.name
  form.email = userStore.profile.email

  loading.value = true
  try {
    const mno = userStore.profile.mno
    // 백엔드에서 리스트 가져오기
    const res = await axios.get(`http://localhost:8080/api/v1/members/${mno}/favorites`)
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
      newPassword: form.newPassword,
    }
    await axios.put('http://localhost:8080/api/v1/members', payload)

    // 성공 메시지 표시
    const successMessage = document.createElement('div')
    successMessage.className = 'fixed top-4 right-4 bg-emerald-100 border-l-4 border-emerald-500 text-emerald-700 p-4 rounded shadow-md z-50'
    successMessage.innerHTML = `
      <div class="flex items-center">
        <svg class="h-6 w-6 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
        </svg>
        <p>회원 정보가 성공적으로 수정되었습니다.</p>
      </div>
    `
    document.body.appendChild(successMessage)

    // 3초 후 메시지 제거
    setTimeout(() => {
      document.body.removeChild(successMessage)
    }, 3000)

    userStore.profile.name = form.name
    // 비밀번호 필드 초기화
    form.currentPassword = ''
    form.newPassword = ''
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
  if (val == null) return '-'

  let eok = 0,
    man = 0,
    remainder = 0
  if (val >= 10000) {
    man = Math.floor(val / 10000)
    remainder = val % 10000
    if (man >= 10000) {
      eok = Math.floor(man / 10000)
      man = man % 10000
    }
  } else {
    remainder = val
  }

  return `${eok > 0 ? eok + '억' : ''}${man > 0 ? man + '만' : ''}${remainder != 0 ? remainder + '원' : '원'}`
}

// 즐겨찾기 삭제
async function removeFavorite(aptSeq) {
  try {
    const mno = userStore.profile.mno
    await axios.delete(`http://localhost:8080/api/v1/members/${mno}/favorites/${aptSeq}`)

    // 로컬 상태 업데이트
    favorites.value = favorites.value.filter(item => item.aptSeq !== aptSeq)
    userStore.favoriteSeqs = userStore.favoriteSeqs.filter(seq => seq !== aptSeq)

    // 성공 메시지 표시
    const successMessage = document.createElement('div')
    successMessage.className = 'fixed top-4 right-4 bg-emerald-100 border-l-4 border-emerald-500 text-emerald-700 p-4 rounded shadow-md z-50'
    successMessage.innerHTML = `
      <div class="flex items-center">
        <svg class="h-6 w-6 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
        </svg>
        <p>즐겨찾기에서 삭제되었습니다.</p>
      </div>
    `
    document.body.appendChild(successMessage)

    // 3초 후 메시지 제거
    setTimeout(() => {
      document.body.removeChild(successMessage)
    }, 3000)

  } catch (e) {
    console.error('즐겨찾기 삭제 실패', e)
    alert('즐겨찾기 삭제 중 오류가 발생했습니다.')
  }
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
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

.backdrop-blur-sm {
  backdrop-filter: blur(4px);
}
</style>
