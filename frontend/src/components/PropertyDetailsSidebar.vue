<template>
  <transition name="slide" appear>
    <div
      v-if="isOpen && detail"
      class="flex flex-col h-full overflow-hidden transition-transform duration-300 ease-in-out w-full"
      :class="isOpen ? 'translate-x-0' : 'translate-x-full'"
    >
      <div class="h-full overflow-auto">
        <!-- 헤더 -->
        <div class="p-4 border-b flex items-center justify-between sticky top-0 bg-white z-10">
          <h3 class="font-semibold text-lg">{{ detail.aptNm || '로딩 중…' }}</h3>
          <button class="p-2 rounded-full hover:bg-gray-100" @click="closeSidebar">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="20"
              height="20"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <path d="M18 6 6 18" />
              <path d="m6 6 12 12" />
            </svg>
          </button>
        </div>

        <div class="p-4">
          <!-- 로딩 스피너 -->
          <div v-if="loading" class="flex items-center justify-center h-64">
            <svg
              class="animate-spin h-12 w-12 text-emerald-600"
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

          <!-- 에러 메시지 -->
          <div v-else-if="error" class="text-red-600 text-center py-8">
            {{ error }}
          </div>

          <!-- 상세 정보 표시 -->
          <div v-else>
            <!-- 이미지 -->
            <img
              :src="detail.imgPath || catPlaceholder"
              :alt="detail.aptNm"
              class="w-full h-56 rounded-lg mb-4"
              :class="detail.imgPath ? 'object-cover' : 'object-contain bg-gray-100 p-4'"
            />

            <!-- 기본 정보 -->
            <div class="flex items-center justify-between mb-4">
              <span class="px-2 py-1 bg-emerald-100 text-emerald-800 text-sm rounded-md">
                {{ detail.propertyType }}
              </span>
              <div class="text-emerald-600 font-medium flex items-center">
                {{ detail.latestSpec }}
              </div>
            </div>

            <!-- 매물 설명(description) -->
            <div class="mb-4">
              <h4 class="font-medium mb-1">매물 설명</h4>
              <p class="text-sm text-gray-700">{{ detail.description || '설명 없음' }}</p>
            </div>

            <!-- 주요 정보 그리드 -->
            <div class="grid grid-cols-2 gap-4 mb-6">
              <div class="flex flex-col">
                <span class="text-sm text-gray-500">가격</span>
                <span class="font-semibold">{{ formatCurrency(detail.latestPrice) }}</span>
              </div>
              <div class="flex flex-col">
                <span class="text-sm text-gray-500">면적</span>
                <span
                  v-if="detail.areaMin !== null && detail.areaMax !== null"
                  class="font-semibold"
                >
                  {{ detail.areaMin }}ᵐ² ~ {{ detail.areaMax }}ᵐ²
                </span>
                <span v-else class="font-semibold">-</span>
              </div>
              <div class="flex flex-col">
                <span class="text-sm text-gray-500">위치</span>
                <span class="font-semibold flex items-center">
                  {{ detail.roadAddress }}
                </span>
              </div>
              <div class="flex flex-col">
                <span class="text-sm text-gray-500">최근 거래</span>
                <span class="font-semibold">{{ detail.lastTradeDetail }}</span>
              </div>
            </div>

            <!-- 탭 메뉴 & 콘텐츠 -->
            <div class="border-b mb-4">
              <div class="flex">
                <button
                  v-for="tab in tabs"
                  :key="tab.id"
                  class="px-4 py-2 flex-1 flex items-center justify-center"
                  :class="
                    activeTab === tab.id
                      ? 'border-b-2 border-emerald-600 font-medium text-emerald-600'
                      : 'text-gray-500'
                  "
                  @click="activeTab = tab.id"
                >
                  {{ tab.name }}
                </button>
              </div>
            </div>

            <!-- 기본 정보 탭 -->
            <div v-if="activeTab === 'info'" class="space-y-4">
              <div class="border rounded-md p-4">
                <h4 class="font-medium mb-2">매매·전세 범위</h4>
                <p class="text-sm">
                  매매: {{ formatCurrency(detail.tradePriceMin) }} ~
                  {{ formatCurrency(detail.tradePriceMax) }}
                </p>
                <p class="text-sm">
                  전세: {{ formatCurrency(detail.jeonsePriceMin) }} ~
                  {{ formatCurrency(detail.jeonsePriceMax) }}
                </p>
              </div>
            </div>

            <!-- 학교 탭 -->
            <div v-if="activeTab === 'schools'" class="border rounded-md p-4">
              <h4 class="font-medium mb-2">주변 학교</h4>
              <ul class="space-y-2 text-sm">
                <li v-for="(sc, idx) in schools" :key="idx" class="flex justify-between">
                  <span>{{ sc.schoolName }} ({{ sc.schoolType }})</span>
                  <span>{{ sc.distance }}</span>
                </li>
              </ul>
            </div>
          </div>
          <!-- 하단 버튼 -->
          <div class="mt-6 flex gap-2">
            <button
              class="flex-1 px-4 py-3"
              :class="
                isFavorite
                  ? 'border border-red-600 text-red-600 hover:bg-red-50 rounded-md '
                  : 'bg-emerald-600 text-white hover:bg-emerald-700 rounded-md'
              "
              @click="$emit('toggle-favorite', aptSeq)"
            >
              {{ isFavorite ? '관심 매물 해제' : '관심 매물 등록' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, watch } from 'vue'
import axios from 'axios'
import catPlaceholder from '@/assets/cat-placeholder.png'

const props = defineProps({
  property: {
    type: Object,
    default: null,
  },
  isOpen: {
    type: Boolean,
    default: false,
  },
  isFavorite: { type: Boolean, default: false },
  aptSeq: { type: String, required: true },
})
const emit = defineEmits(['close', 'toggle-favorite', 'consult'])

const activeTab = ref('info')

// 아이콘 컴포넌트
// const HomeIcon = {
//   template: `
//     <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
//   `,
// }

// const CalendarIcon = {
//   template: `
//     <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="18" height="18" x="3" y="4" rx="2" ry="2"/><line x1="16" x2="16" y1="2" y2="6"/><line x1="8" x2="8" y1="2" y2="6"/><line x1="3" x2="21" y1="10" y2="10"/></svg>
//   `,
// }

// const BarChartIcon = {
//   template: `
//     <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" x2="12" y1="20" y2="10"/><line x1="18" x2="18" y1="20" y2="4"/><line x1="6" x2="6" y1="20" y2="16"/></svg>
//   `,
// }
const loading = ref(false)
const error = ref('')
const detail = ref(null)
const schools = ref([])

const tabs = [
  { id: 'info', name: '정보' },
  { id: 'schools', name: '학교' },
]

// 사이드바 열림/닫힘 로그
watch(
  () => props.isOpen,
  (open) => {
    if (open) {
      console.log('🛈 사이드바 열림, aptSeq=', props.aptSeq)
    }
  },
)

// 데이터 fetch
watch(
  () => props.aptSeq,
  async (seq) => {
    if (!seq) return
    loading.value = true
    error.value = ''
    console.log('🛎️ API 호출 시작 for aptSeq=', seq)
    try {
      const [dRes, sRes] = await Promise.all([
        axios.get(`http://localhost:8080/api/v1/house/${seq}/detail`),
        axios.get(`http://localhost:8080/api/v1/house/${seq}/schools`),
      ])
      console.log('✅ /detail →', dRes.data)
      console.log('✅ /schools →', sRes.data)
      detail.value = dRes.data
      schools.value = sRes.data
    } catch (e) {
      console.error('🔴 API 호출 오류', e)
      error.value = '데이터를 불러오는 중 오류가 발생했습니다.'
    } finally {
      loading.value = false
    }
  },
  { immediate: true },
)

function closeSidebar() {
  emit('close')
}

function formatCurrency(val) {
  return val != null ? val.toLocaleString() + '원' : '-'
}
</script>

<style scoped>
.slide-enter-from {
  transform: translateX(-100%);
}

.slide-enter-to {
  transform: translateX(0);
}

.slide-leave-from {
  transform: translateX(0);
}

.slide-leave-to {
  transform: translateX(-100%);
}

.slide-enter-active,
.slide-leave-active {
  transition: transform 0.3s ease-in-out;
}
</style>
