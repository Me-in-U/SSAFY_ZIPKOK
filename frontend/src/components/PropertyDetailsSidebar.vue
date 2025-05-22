<template>
  <transition name="slide" appear>
    <div
      v-if="isOpen"
      class="flex flex-col h-full overflow-hidden transition-transform duration-300 ease-in-out w-full"
      :class="isOpen ? 'translate-x-0' : 'translate-x-full'"
    >
      <div class="h-full overflow-auto p-4">
        <!-- 1) 로딩 스피너 -->
        <div v-if="loading" class="flex items-center justify-center h-full">
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
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z" />
          </svg>
        </div>

        <!-- 2) 에러 메시지 -->
        <div v-else-if="error" class="text-red-600 text-center py-8">
          {{ error }}
        </div>

        <!-- 3) 상세 정보 -->
        <div v-else-if="detail">
          <!-- 헤더 -->
          <div
            class="pt-1 pb-1 pl-4 border-b flex items-center justify-between sticky top-0 bg-white z-10 border rounded-lg"
          >
            <h3 class="font-semibold text-lg">{{ detail.aptNm }}</h3>
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

          <!-- 매물 설명 -->
          <div class="mb-4">
            <h4 class="font-medium mb-1">매물 설명</h4>
            <p class="text-sm text-gray-700">{{ detail.description || '설명 없음' }}</p>
          </div>

          <!-- 주요 정보 그리드 -->
          <div class="grid grid-cols-2 gap-4 mb-6">
            <div class="flex flex-col">
              <span class="text-sm text-gray-500">가격</span>
              <span class="font-semibold">{{ priceDisplay }}</span>
            </div>
            <div class="flex flex-col">
              <span class="text-sm text-gray-500">면적</span>
              <span v-if="detail.areaMin !== null && detail.areaMax !== null" class="font-semibold">
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

          <!-- 탭 메뉴 -->
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

          <!-- 정보 탭 -->
          <div v-show="activeTab === 'info'" class="space-y-4">
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
            <!-- 면적별 필터 드롭다운 -->
            <div class="flex items-center gap-2">
              <label class="text-sm text-gray-700">면적 선택:</label>
              <select v-model="areaFilter" class="border rounded px-2 py-1 text-sm">
                <option value="all">전체</option>
                <option v-for="ar in areaOptions" :key="ar" :value="ar">{{ ar }}㎡</option>
              </select>
            </div>

            <!-- 거래 내역 그래프 -->
            <div class="border rounded-md p-4 h-80 flex flex-col overflow-hidden">
              <h4 class="font-medium mb-2">월별 매매 가격 추이</h4>
              <div class="flex-1">
                <canvas ref="dealsChart" class="w-full h-full"></canvas>
              </div>
            </div>
          </div>

          <!-- 학교 탭 -->
          <div v-show="activeTab === 'schools'" class="border rounded-md p-4">
            <h4 class="font-medium mb-2">주변 학교</h4>
            <ul class="space-y-2 text-sm">
              <li v-for="(sc, idx) in schools" :key="idx" class="flex justify-between">
                <span>{{ sc.schoolName }} ({{ sc.schoolType }})</span>
                <span>{{ sc.distance }}</span>
              </li>
            </ul>
          </div>

          <!-- 하단 버튼 -->
          <div class="mt-6 flex gap-2">
            <button
              class="flex-1 px-4 py-3"
              :class="
                isFavorite
                  ? 'border border-red-600 text-red-600 hover:bg-red-50 rounded-md'
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
import { ref, watch, nextTick, computed } from 'vue'
import axios from 'axios'
import catPlaceholder from '@/assets/cat-placeholder.png'
import { Chart, registerables } from 'chart.js'
Chart.register(...registerables)

// emit props
const emit = defineEmits(['close', 'toggle-favorite'])
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

// refs
const activeTab = ref('info')
const loading = ref(false)
const error = ref('')
const detail = ref(null)
const schools = ref([])
const dealsData = ref([])
const dealsChart = ref(null)
const areaFilter = ref('all') // 선택된 면적 (‘all’ or 숫자)

// 데이터가 바뀔 때마다 고유 면적 목록을 뽑아서 정렬
const areaOptions = computed(() => {
  const set = new Set(dealsData.value.map((d) => Number(d.excluUseAr)))
  return Array.from(set).sort((a, b) => a - b)
})

// constants
const tabs = [
  { id: 'info', name: '정보' },
  { id: 'schools', name: '학교' },
]
let chartInstance = null

// 사이드바 열림/닫힘 로그
watch(
  () => props.isOpen,
  (open) => {
    if (open) {
      console.log('🛈 사이드바 열림, aptSeq=', props.aptSeq)
    }
  },
)
// dealType 기반으로 보여줄 가격 문자열 생성
const priceDisplay = computed(() => {
  console.log('🟢 priceDisplay', detail.value)
  if (!detail.value) return '-'
  const { dealType, deposit, monthlyRent, latestPrice } = detail.value

  if (dealType === '월세') {
    // deposit: 보증금, monthlyRent: 월세
    return `${formatCurrency(deposit)} / ${formatCurrency(monthlyRent)}`
  }
  // 전세나 매매는 latestPrice 필드 사용
  return formatCurrency(latestPrice)
})

watch(
  () => props.aptSeq,
  async (seq) => {
    if (!seq) return
    loading.value = true
    error.value = ''
    try {
      // (1) 기존 detail & schools API 호출
      const [dRes, sRes, dealsRes] = await Promise.all([
        axios.get(`https://api.ssafy.blog/api/v1/house/${seq}/detail`),
        axios.get(`https://api.ssafy.blog/api/v1/house/${seq}/schools`),
        axios.get(`https://api.ssafy.blog/api/v1/house/${seq}/dealsDone`),
      ])
      detail.value = dRes.data
      schools.value = sRes.data
      dealsData.value = dealsRes.data
      console.log('🟢 상세 정보', detail.value)
    } catch (e) {
      console.error('🔴 API 오류', e)
      error.value = '데이터를 불러오는 중 오류가 발생했습니다.'
    } finally {
      loading.value = false
      if (activeTab.value === 'info') {
        await drawChart()
      }
    }
  },
  { immediate: true },
)

// 탭이 info 로 바뀔 때
watch([activeTab, areaFilter], ([tab]) => {
  if (tab === 'info') {
    drawChart()
  }
})
async function drawChart() {
  if (activeTab.value !== 'info') return
  await nextTick()
  const canvas = dealsChart.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  if (chartInstance) chartInstance.destroy()

  // 전체 거래 내역 중 필터링(금액이 있는 것만)
  const allValid = dealsData.value.filter((d) => d.dealAmount)

  // (1) 공통 라벨: YYYY-MM 형태로 유니크하게 정렬
  const allLabels = Array.from(
    new Set(allValid.map((d) => `${d.dealYear}-${String(d.dealMonth).padStart(2, '0')}`)),
  ).sort()

  const datasets = []

  if (areaFilter.value === 'all') {
    // (2) 면적 옵션마다 데이터 생성
    areaOptions.value.forEach((area) => {
      const filtered = allValid.filter((d) => Number(d.excluUseAr) === area)
      // 라벨에 맞춰 값 매핑, 없으면 null
      const data = allLabels.map((label) => {
        const rec = filtered.find((d) => {
          const m = `${d.dealYear}-${String(d.dealMonth).padStart(2, '0')}`
          return m === label
        })
        return rec ? Number(rec.dealAmount.replace(/,/g, '')) : null
      })
      datasets.push({
        label: `${area}㎡`,
        data,
        fill: false,
        tension: 0.2,
      })
    })
  } else {
    // 단일 면적 필터 모드
    const filtered = allValid.filter((d) => Number(d.excluUseAr) === Number(areaFilter.value))
    const data = allLabels.map((label) => {
      const rec = filtered.find((d) => {
        const m = `${d.dealYear}-${String(d.dealMonth).padStart(2, '0')}`
        return m === label
      })
      return rec ? Number(rec.dealAmount.replace(/,/g, '')) : null
    })
    datasets.push({
      label: `${areaFilter.value}㎡ 거래가`,
      data,
      fill: false,
      tension: 0.2,
    })
  }

  chartInstance = new Chart(ctx, {
    type: 'line',
    data: {
      labels: allLabels,
      datasets,
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        x: {
          title: { display: true, text: '거래 월' },
        },
        y: { title: { display: true, text: '가격(만원)' } },
      },
    },
  })
}

function closeSidebar() {
  emit('close')
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
  }

  return `${eok > 0 ? eok + '억' : ''}${man > 0 ? man + '만' : ''}${remainder != 0 ? remainder + '원' : '원'}`
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
