<template>
  <transition name="slide" appear>
    <!-- fixed 컨테이너는 한 번만! -->
    <div
      v-if="isOpen && property"
      class="flex flex-col h-full overflow-hidden transition-transform duration-300 ease-in-out w-full"
      :class="isOpen ? 'translate-x-0' : 'translate-x-full'"
    >
      <div class="h-full overflow-auto">
        <!-- 헤더 -->
        <div class="p-4 border-b flex items-center justify-between sticky top-0 bg-white z-10">
          <h3 class="font-semibold text-lg">{{ property.title }}</h3>
          <button class="p-2 rounded-full hover:bg-gray-100" @click="$emit('close')">
            <!-- 닫기 아이콘 -->
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
          <!-- 이미지 -->
          <img
            :src="property.image || '/placeholder.svg'"
            :alt="property.title"
            class="w-full h-56 object-cover rounded-lg mb-4"
          />

          <!-- 기본 정보 -->
          <div class="flex items-center justify-between mb-4">
            <span class="px-2 py-1 bg-emerald-100 text-emerald-800 text-sm rounded-md">
              {{ property.type }}
            </span>
            <div class="text-emerald-600 font-medium flex items-center">
              <!-- 화살표 아이콘 (예시) -->
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="16"
                height="16"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
                class="mr-1"
              >
                <polyline points="22 7 13.5 15.5 8.5 10.5 2 17" />
                <polyline points="16 7 22 7 22 13" />
              </svg>
              {{ property.priceChange }} (1년)
            </div>
          </div>

          <!-- 주요 정보 그리드 -->
          <div class="grid grid-cols-2 gap-4 mb-6">
            <div class="flex flex-col">
              <span class="text-sm text-gray-500">가격</span>
              <span class="font-semibold">{{ property.price }}</span>
            </div>
            <div class="flex flex-col">
              <span class="text-sm text-gray-500">면적</span>
              <span class="font-semibold">{{ property.size }}</span>
            </div>
            <div class="flex flex-col">
              <span class="text-sm text-gray-500">위치</span>
              <span class="font-semibold flex items-center">
                <!-- 위치 아이콘 -->
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="12"
                  height="12"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  class="mr-1"
                >
                  <path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z" />
                  <circle cx="12" cy="10" r="3" />
                </svg>
                {{ property.location }}
              </span>
            </div>
            <div class="flex flex-col">
              <span class="text-sm text-gray-500">예상 수익률</span>
              <span class="font-semibold text-emerald-600">{{ property.roi }}</span>
            </div>
          </div>

          <!-- 탭 메뉴 -->
          <div class="border-b mb-4">
            <div class="flex">
              <button
                v-for="tab in tabs"
                :key="tab.id"
                class="px-4 py-2 flex-1 flex items-center justify-center"
                :class="{
                  'border-b-2 border-emerald-600 font-medium text-emerald-600':
                    activeTab === tab.id,
                  'text-gray-500': activeTab !== tab.id,
                }"
                @click="activeTab = tab.id"
              >
                <component :is="tab.icon" class="w-4 h-4 mr-2" />
                {{ tab.name }}
              </button>
            </div>
          </div>

          <!-- 탭 콘텐츠 -->
          <!-- 기본 정보 탭 -->
          <div v-if="activeTab === 'info'" class="space-y-4">
            <div class="border rounded-md p-4">
              <h4 class="font-medium mb-2">매물 정보</h4>
              <ul class="space-y-2 text-sm">
                <li class="flex justify-between">
                  <span class="text-gray-500">건물 유형</span>
                  <span>{{ property.type }}</span>
                </li>
                <li class="flex justify-between">
                  <span class="text-gray-500">준공년도</span>
                  <span>{{ property.builtYear }}년</span>
                </li>
                <li class="flex justify-between">
                  <span class="text-gray-500">주소</span>
                  <span>{{ property.address }}</span>
                </li>
                <li class="flex justify-between">
                  <span class="text-gray-500">거래 유형</span>
                  <span>{{ property.dealType }}</span>
                </li>
                <li class="flex justify-between">
                  <span class="text-gray-500">입주 가능일</span>
                  <span>{{ property.possibleDay }}</span>
                </li>
              </ul>
            </div>

            <div class="border rounded-md p-4">
              <h4 class="font-medium mb-2">시설 정보</h4>
              <div class="flex flex-wrap gap-2">
                <span
                  v-for="(amenity, index) in property.amenities"
                  :key="index"
                  class="px-2 py-1 bg-gray-100 text-sm rounded-md"
                >
                  {{ amenity }}
                </span>
              </div>
            </div>

            <div class="border rounded-md p-4">
              <h4 class="font-medium mb-2">매물 설명</h4>
              <p class="text-sm text-gray-700">{{ property.description }}</p>
            </div>
          </div>

          <!-- 가격 이력 탭 -->
          <div v-if="activeTab === 'history'" class="border rounded-md p-4">
            <h4 class="font-medium mb-4">가격 변동 이력</h4>
            <div class="space-y-3">
              <div class="flex justify-between items-center pb-2 border-b">
                <span class="text-sm">2023년 12월</span>
                <span class="font-medium">{{ property.price }}</span>
              </div>
              <div class="flex justify-between items-center pb-2 border-b">
                <span class="text-sm">2023년 6월</span>
                <span class="font-medium">{{ getPreviousPrice(property.priceValue, -2.1) }}</span>
              </div>
              <div class="flex justify-between items-center pb-2 border-b">
                <span class="text-sm">2022년 12월</span>
                <span class="font-medium">{{ getPreviousPrice(property.priceValue, -4.5) }}</span>
              </div>
              <div class="flex justify-between items-center">
                <span class="text-sm">2022년 6월</span>
                <span class="font-medium">{{ getPreviousPrice(property.priceValue, -6.2) }}</span>
              </div>
            </div>

            <!-- 가격 차트 (플레이스홀더) -->
            <div class="mt-6 h-48 bg-gray-100 rounded-md flex items-center justify-center">
              <p class="text-gray-500 text-sm">가격 변동 차트가 여기에 표시됩니다</p>
            </div>
          </div>

          <!-- 투자 분석 탭 -->
          <div v-if="activeTab === 'analysis'" class="space-y-4">
            <div class="border rounded-md p-4">
              <h4 class="font-medium mb-2">투자 분석</h4>
              <div class="space-y-3">
                <div class="flex justify-between items-center">
                  <span class="text-gray-500">예상 월 임대료</span>
                  <span class="font-medium">{{ getMonthlyRent(property) }}</span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="text-gray-500">연간 수익률</span>
                  <span class="font-medium text-emerald-600">{{ property.roi }}</span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="text-gray-500">투자 회수 기간</span>
                  <span class="font-medium">약 {{ getPaybackPeriod(property) }}년</span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="text-gray-500">주변 시세 대비</span>
                  <span class="font-medium text-emerald-600">-2.3%</span>
                </div>
              </div>
            </div>

            <div class="border rounded-md p-4">
              <h4 class="font-medium mb-2">투자 위험도</h4>
              <div class="w-full bg-gray-200 rounded-full h-2.5 mb-4">
                <div class="bg-emerald-600 h-2.5 rounded-full" style="width: 65%"></div>
              </div>
              <div class="flex justify-between text-xs text-gray-500">
                <span>낮음</span>
                <span>중간</span>
                <span>높음</span>
              </div>
            </div>

            <div class="border rounded-md p-4">
              <h4 class="font-medium mb-2">AI 투자 분석 리포트</h4>
              <p class="text-sm text-gray-700 mb-4">
                이 매물은 {{ property.location }} 지역의 평균 대비 투자 가치가 높습니다. 최근 3년간
                {{ property.priceChange }} 상승했으며, 향후 2년간 약 3~5%의 추가 상승이 예상됩니다.
                교통 편의성과 주변 인프라를 고려할 때 임대 수요가 안정적일 것으로 예상됩니다.
              </p>
              <button
                class="w-full py-2 bg-gray-100 text-gray-700 rounded-md text-sm hover:bg-gray-200 transition"
              >
                전체 리포트 보기
              </button>
            </div>
          </div>

          <!-- 하단 버튼 -->
          <div class="mt-6 flex gap-2">
            <button
              class="flex-1 px-4 py-3 bg-emerald-600 text-white rounded-md hover:bg-emerald-700 transition"
            >
              투자 상담 받기
            </button>
            <button
              class="flex-1 px-4 py-3 border border-emerald-600 text-emerald-600 rounded-md hover:bg-emerald-50 transition"
            >
              관심 매물 등록
            </button>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, defineProps, defineEmits } from 'vue'

// eslint-disable-next-line no-unused-vars
const props = defineProps({
  property: {
    type: Object,
    default: null,
  },
  isOpen: {
    type: Boolean,
    default: false,
  },
})

defineEmits(['close'])

const activeTab = ref('info')

// 아이콘 컴포넌트
const HomeIcon = {
  template: `
    <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
  `,
}

const CalendarIcon = {
  template: `
    <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="18" height="18" x="3" y="4" rx="2" ry="2"/><line x1="16" x2="16" y1="2" y2="6"/><line x1="8" x2="8" y1="2" y2="6"/><line x1="3" x2="21" y1="10" y2="10"/></svg>
  `,
}

const BarChartIcon = {
  template: `
    <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" x2="12" y1="20" y2="10"/><line x1="18" x2="18" y1="20" y2="4"/><line x1="6" x2="6" y1="20" y2="16"/></svg>
  `,
}

const tabs = [
  { id: 'info', name: '기본 정보', icon: HomeIcon },
  { id: 'history', name: '가격 이력', icon: CalendarIcon },
  { id: 'analysis', name: '투자 분석', icon: BarChartIcon },
]

// 이전 가격 계산 (백분율 변화 기준)
function getPreviousPrice(currentPrice, percentChange) {
  const previousPrice = currentPrice / (1 + Math.abs(percentChange) / 100)

  // 억 단위로 변환
  const billions = Math.floor(previousPrice / 100000000)
  const millions = Math.floor((previousPrice % 100000000) / 10000)

  if (millions > 0) {
    return `${billions}억 ${millions.toLocaleString()}만원`
  } else {
    return `${billions}억원`
  }
}

// 월 임대료 계산 (예상)
function getMonthlyRent(property) {
  // 간단한 계산: 매매가의 약 0.4~0.5% 정도가 월 임대료
  const monthlyRentRate = 0.0045 // 0.45%
  const monthlyRent = property.priceValue * monthlyRentRate

  // 만원 단위로 반올림
  const roundedRent = Math.round(monthlyRent / 10000) * 10000
  return `${(roundedRent / 10000).toLocaleString()}만원`
}

// 투자 회수 기간 계산
function getPaybackPeriod(property) {
  // ROI 문자열에서 숫자만 추출 (예: "4.2%" -> 4.2)
  const roiValue = parseFloat(property.roi)

  // 투자 회수 기간 = 100 / 연간 수익률
  return Math.round(100 / roiValue)
}
</script>

<style scoped>
/* enter */
.slide-enter-from {
  transform: translateX(-100%);
}
.slide-enter-to {
  transform: translateX(0);
}
/* leave */
.slide-leave-from {
  transform: translateX(0);
}
.slide-leave-to {
  transform: translateX(-100%);
}

/* 공통 */
.slide-enter-active,
.slide-leave-active {
  transition: transform 0.3s ease-in-out;
}
</style>
