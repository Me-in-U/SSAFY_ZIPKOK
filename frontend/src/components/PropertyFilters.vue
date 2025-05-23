<template>
  <!-- 1. 지역 선택 + 검색 한 줄에 -->
  <div class="flex flex-nowrap items-center bg-white/70 p-2 rounded-full shadow-lg">
    <!-- 시·도 선택 (왼쪽만 둥글게) -->
    <select
      v-model="filters.sido"
      @change="onSidoChange"
      class="flex-shrink-0 p-1.5 border border-r-0 rounded-l-full focus:ring-emerald-500"
    >
      <option value="">시·도 선택</option>
      <option v-for="s in sidoList" :key="s" :value="s">{{ s }}</option>
    </select>

    <!-- 시·군·구 선택 (중간 경계만) -->
    <select
      v-model="filters.gugun"
      @change="onGugunChange"
      @mousedown="onGugunMouseDown"
      class="flex-shrink-0 p-1.5 border border-r-0 border-l-0 focus:ring-emerald-500 disabled:opacity-100 disabled:cursor-not-allowed"
    >
      <option value="">시·군·구 선택</option>
      <option v-for="g in gugunList" :key="g" :value="g">{{ g }}</option>
    </select>

    <!-- 읍·면·동·리 선택 (중간 경계만) -->
    <select
      v-model="filters.dong"
      @change="onDongChange"
      @mousedown="onDongMouseDown"
      class="flex-shrink-0 p-1.5 border border-r-0 border-l-0 focus:ring-emerald-500 disabled:opacity-100 disabled:cursor-not-allowed"
    >
      <option value="">읍·면·동·리 선택</option>
      <option v-for="d in dongList" :key="d" :value="d">{{ d }}</option>
    </select>

    <!-- 단지명 검색 + 검색 버튼 -->
    <div class="flex flex-shrink-0">
      <div class="relative">
        <input
          id="search"
          v-model="searchQuery"
          @keyup.enter="onSearch"
          type="text"
          placeholder=" "
          class="peer border-l-0 border-r-0 w-48 h-full pl-3 border focus:outline-none focus:ring-emerald-500"
        />
        <label
          for="search"
          class="absolute left-2 bg-white -top-2 transition-all duration-200 text-xs text-gray-500 peer-placeholder-shown:top-1.5 peer-placeholder-shown:text-base peer-focus:-top-2 peer-focus:text-xs peer-focus:text-emerald-600"
        >
          단지명 검색
        </label>
      </div>
      <!-- 검색 버튼 (오른쪽만 둥글게) -->
      <button
        @click="onSearch"
        class="px-3 py-1.5 mr-4 bg-emerald-600 border text-white rounded-r-full hover:bg-emerald-700 transition"
      >
        검색
      </button>
    </div>
  </div>

  <!-- 3. 결과 텍스트 -->
  <!-- 3. 결과 텍스트 -->
  <div class="flex justify-center mt-2 text-sm text-red-600">
    <div v-if="errorMsg">{{ errorMsg }}</div>
    <div v-else-if="noResults">검색결과가 없습니다.</div>
    <div v-else-if="hasResults" class="text-gray-700">
      {{ filters.sido }} {{ filters.gugun }} {{ filters.dong }} {{ searchQuery }}에 대한 검색
      결과입니다.
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'

// eslint-disable-next-line no-unused-vars
const props = defineProps({
  noResults: Boolean,
  hasResults: Boolean,
  showBase: Boolean,
  showFavorite: Boolean,
  showSearch: Boolean,
})

// emit 정의
const emit = defineEmits([
  'filter-change',
  'move-to',
  'toggle-base',
  'toggle-favorite',
  'toggle-search',
])

// 검색어와 에러 메시지 상태 관리
const searchQuery = ref('')
const errorMsg = ref('')

// reactive()를 사용하여 필터 상태 관리
const filters = reactive({
  sido: '',
  gugun: '',
  dong: '',
})

const sidoList = ref([])
const gugunList = ref([])
const dongList = ref([])

onMounted(async () => {
  const { data } = await axios.get('https://api.ssafy.blog/api/v1/sidogungu/sido')
  sidoList.value = data
})

// 지도 이동용 주소 문자열 생성
function buildAddress() {
  return [filters.dong, filters.gugun, filters.sido].filter(Boolean).join(' ')
}

// 시·도 변경
async function onSidoChange() {
  filters.gugun = ''
  filters.dong = ''
  dongList.value = []
  if (filters.sido) {
    const { data } = await axios.get(
      `https://api.ssafy.blog/api/v1/sidogungu/gugun/${filters.sido}`,
    )
    gugunList.value = data
  } else {
    gugunList.value = []
  }
  emit('move-to', { address: buildAddress() })
  setMessage('')
}

// 구·군 변경
async function onGugunChange() {
  filters.dong = ''
  if (filters.gugun) {
    const { data } = await axios.get(
      `https://api.ssafy.blog/api/v1/sidogungu/dong/${filters.sido}/${filters.gugun}`,
    )
    dongList.value = data
  } else {
    dongList.value = []
  }
  emit('move-to', { address: buildAddress() })
  setMessage('')
}

// 읍·면·동 변경
function onDongChange() {
  emit('move-to', { address: buildAddress() })
  setMessage('')
}

// 검색 실행
async function onSearch() {
  if (!searchQuery.value) {
    setMessage('검색어가 없습니다.')
    return
  }

  try {
    // response.data가 바로 HouseInfo[] 배열
    const response = await axios.get('http://localhost:8080/api/v1/house/filter', {
      params: {
        sido: filters.sido || undefined,
        gugun: filters.gugun || undefined,
        dong: filters.dong || undefined,
        aptNm: searchQuery.value || undefined,
      },
    })
    const houses = response.data
    console.log('[검색결과]:', houses, `(${houses.length}개)`)
    // 부모 컴포넌트로 결과 전달
    emit('search-filter', houses || [])
  } catch (err) {
    console.error('필터 검색 실패', err)
  }
}

function setMessage(msg) {
  errorMsg.value = msg
}

function onGugunMouseDown(e) {
  if (!filters.sido) {
    setMessage('시도를 먼저 선택해주세요')
    e.preventDefault()
  }
}

function onDongMouseDown(e) {
  if (!filters.gugun) {
    setMessage('시군구를 먼저 선택해주세요')
    e.preventDefault()
  }
}
</script>
<style scoped>
select {
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
}
</style>
