<template>
  <div class="bg-white rounded-lg shadow p-4">
    <div class="flex flex-col gap-4">
      <!-- 검색 바 -->
      <div class="flex flex-col md:flex-row gap-2">
        <div class="relative flex-1">
          <div class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400">
            <svg
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
              <circle cx="11" cy="11" r="8" />
              <path d="m21 21-4.3-4.3" />
            </svg>
          </div>
          <input
            type="text"
            placeholder="지역, 단지명, 매물번호 검색"
            v-model="searchQuery"
            class="w-full pl-10 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-emerald-500"
          />
        </div>
        <button
          class="p-2 border rounded-md hover:bg-gray-50"
          @click="showAdvanced = !showAdvanced"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="20"
            height="20"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <line x1="21" x2="3" y1="4" y2="4" />
            <line x1="21" x2="3" y1="12" y2="12" />
            <line x1="21" x2="3" y1="20" y2="20" />
            <polyline points="8 8 4 4 8 0" />
            <polyline points="16 16 20 20 16 24" />
          </svg>
        </button>
        <button
          class="px-4 py-2 bg-emerald-600 text-white rounded-md hover:bg-emerald-700 transition"
          @click="applyFilters"
        >
          검색
        </button>
      </div>

      <!-- 상세 필터 -->
      <div v-if="showAdvanced" class="border rounded-md p-4">
        <div class="mb-4 flex justify-between items-center">
          <h3 class="font-medium text-gray-700">상세 필터</h3>
          <button class="text-sm text-emerald-600 hover:text-emerald-700" @click="resetFilters">
            필터 초기화
          </button>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 lg:grid-cols-4 gap-4">
          <!-- 1) 시·도 -->
          <div class="space-y-2">
            <label class="text-sm font-medium text-gray-700">시·도</label>
            <select
              v-model="filters.sido"
              @change="onSidoChange"
              class="w-full p-2 border rounded-md focus:ring-emerald-500"
            >
              <option value="">시·도 선택</option>
              <option v-for="s in sidoList" :key="s" :value="s">
                {{ s }}
              </option>
            </select>
          </div>

          <!-- 2) 구·군 -->
          <div class="space-y-2">
            <label class="text-sm font-medium text-gray-700">구·군</label>
            <select
              v-model="filters.gugun"
              @change="onGugunChange"
              :disabled="!gugunList.length"
              class="w-full p-2 border rounded-md focus:ring-emerald-500 disabled:opacity-50"
            >
              <option value="">구·군 선택</option>
              <option v-for="g in gugunList" :key="g" :value="g">
                {{ g }}
              </option>
            </select>
          </div>

          <!-- 3) 읍·면·동 -->
          <div class="space-y-2">
            <label class="text-sm font-medium text-gray-700">읍·면·동</label>
            <select
              v-model="filters.dong"
              :disabled="!dongList.length"
              class="w-full p-2 border rounded-md focus:ring-emerald-500 disabled:opacity-50"
            >
              <option value="">읍·면·동 선택</option>
              <option v-for="d in dongList" :key="d" :value="d">
                {{ d }}
              </option>
            </select>
          </div>
          <div class="space-y-2">
            <!-- 여기로 “이동” 버튼 추가 -->
            <button
              class="mt-1 px-3 py-1 bg-emerald-500 text-white rounded hover:bg-emerald-600 text-sm"
              :disabled="!filters.dong"
              @click="onMove"
            >
              이동
            </button>
          </div>
          <!-- 매물 유형 -->
          <div class="space-y-2">
            <label class="text-sm font-medium text-gray-700">매물 유형</label>
            <select
              v-model="filters.propertyType"
              class="w-full p-2 border rounded-md focus:ring-emerald-500"
            >
              <option value="">매물 유형 선택</option>
              <option value="아파트">아파트</option>
              <option value="오피스텔">오피스텔</option>
              <option value="빌라">빌라</option>
              <option value="단독주택">단독주택</option>
              <option value="상가">상가</option>
            </select>
          </div>

          <!-- 거래 유형 -->
          <div class="space-y-2">
            <label class="text-sm font-medium text-gray-700">거래 유형</label>
            <select
              v-model="filters.dealType"
              class="w-full p-2 border rounded-md focus:ring-emerald-500"
            >
              <option value="">거래 유형 선택</option>
              <option value="매매">매매</option>
              <option value="전세">전세</option>
              <option value="월세">월세</option>
            </select>
          </div>

          <!-- 가격 범위 -->
          <div class="space-y-2 md:col-span-2">
            <label class="text-sm font-medium text-gray-700">가격 범위 (억원)</label>
            <div class="px-2 pt-6 pb-2">
              <vue-slider
                v-model="filters.priceRange"
                :min="0"
                :max="100"
                :interval="1"
                :dot-size="20"
                :process-style="{ backgroundColor: '#059669' }"
                :tooltip-style="{ backgroundColor: '#059669', borderColor: '#059669' }"
              />
              <div class="flex justify-between mt-2 text-sm text-gray-600">
                <span>0억</span>
                <span>{{ filters.priceRange[0] }}억 ~ {{ filters.priceRange[1] }}억</span>
                <span>100억+</span>
              </div>
            </div>
          </div>

          <!-- 면적 -->
          <div class="space-y-2">
            <label class="text-sm font-medium text-gray-700">면적</label>
            <select
              v-model="filters.area"
              class="w-full p-2 border rounded-md focus:ring-emerald-500"
            >
              <option value="">면적 선택</option>
              <option value="small">~20평</option>
              <option value="medium">20평~30평</option>
              <option value="large">30평~40평</option>
              <option value="xlarge">40평 이상</option>
            </select>
          </div>

          <!-- 추가 필터 버튼 -->
          <div class="flex items-end">
            <button
              class="px-4 py-2 border border-emerald-600 text-emerald-600 rounded-md hover:bg-emerald-50"
              @click="showMoreFilters = !showMoreFilters"
            >
              {{ showMoreFilters ? '접기' : '추가 필터' }}
            </button>
          </div>
        </div>

        <!-- 추가 필터 -->
        <div
          v-if="showMoreFilters"
          class="mt-4 pt-4 border-t grid grid-cols-1 md:grid-cols-3 gap-4"
        >
          <div class="space-y-2">
            <label class="text-sm font-medium text-gray-700">준공년도</label>
            <select
              v-model="filters.builtYear"
              class="w-full p-2 border rounded-md focus:ring-emerald-500"
            >
              <option value="">전체</option>
              <option value="2020">2020년 이후</option>
              <option value="2015">2015년 이후</option>
              <option value="2010">2010년 이후</option>
              <option value="2000">2000년 이후</option>
            </select>
          </div>
          <div class="space-y-2">
            <label class="text-sm font-medium text-gray-700">세대수</label>
            <select
              v-model="filters.household"
              class="w-full p-2 border rounded-md focus:ring-emerald-500"
            >
              <option value="">전체</option>
              <option value="small">300세대 미만</option>
              <option value="medium">300~1,000세대</option>
              <option value="large">1,000세대 이상</option>
            </select>
          </div>
          <div class="space-y-2">
            <label class="text-sm font-medium text-gray-700">입주 가능일</label>
            <select
              v-model="filters.availableIn"
              class="w-full p-2 border rounded-md focus:ring-emerald-500"
            >
              <option value="">전체</option>
              <option value="immediate">즉시 입주</option>
              <option value="1month">1개월 이내</option>
              <option value="3month">3개월 이내</option>
              <option value="6month">6개월 이내</option>
            </select>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import VueSlider from 'vue-slider-component' // 실제 프로젝트에선 npm install 해주세요
import 'vue-slider-component/theme/default.css'

const emit = defineEmits(['filter-change', 'move-to'])

const searchQuery = ref('')
const showAdvanced = ref(false)
const showMoreFilters = ref(false)

// cascade selectors
const sidoList = ref([])
const gugunList = ref([])
const dongList = ref([])

const filters = reactive({
  sido: '',
  gugun: '',
  dong: '',
  propertyType: '',
  dealType: '',
  priceRange: [0, 100],
  area: '',
  builtYear: '',
  household: '',
  availableIn: '',
})
function onMove() {
  emit('move-to', { address: `${filters.dong} ${filters.gugun} ${filters.sido}` })
}

async function loadSido() {
  const res = await axios.get('http://localhost:8080/api/v1/sidogungu/sido')
  sidoList.value = res.data
  console.log('sidoList', sidoList.value)
}
async function onSidoChange() {
  filters.gugun = ''
  filters.dong = ''
  dongList.value = []
  if (!filters.sido) {
    gugunList.value = []
    return
  }
  const res = await axios.get(`http://localhost:8080/api/v1/sidogungu/gugun/${filters.sido}`)
  gugunList.value = res.data
  console.log('gugunList', gugunList.value)
}
async function onGugunChange() {
  filters.dong = ''
  if (!filters.gugun) {
    dongList.value = []
    return
  }
  const res = await axios.get(
    `http://localhost:8080/api/v1/sidogungu/dong/${filters.sido}/${filters.gugun}`,
  )
  dongList.value = res.data
  console.log('dongList', dongList.value)
}

function applyFilters() {
  emit('filter-change', {
    searchQuery: searchQuery.value,
    ...filters,
  })
}
function resetFilters() {
  searchQuery.value = ''
  Object.assign(filters, {
    sido: '',
    gugun: '',
    dong: '',
    propertyType: '',
    dealType: '',
    priceRange: [0, 100],
    area: '',
    builtYear: '',
    household: '',
    availableIn: '',
  })
  gugunList.value = []
  dongList.value = []
  applyFilters()
}

onMounted(loadSido)
</script>

<style scoped>
.bg-white {
  background-color: white;
}
.rounded-lg {
  border-radius: 0.5rem;
}
/* 필요에 따라 추가 스타일 조정하세요 */
</style>
