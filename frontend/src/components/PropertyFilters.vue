<template>
  <div class="bg-white rounded-lg shadow p-4">
    <!-- 1. 지역 선택 -->
    <div class="flex flex-wrap gap-2 mb-4">
      <select v-model="filters.sido" @change="onSidoChange" class="flex-1 p-2 border rounded-md focus:ring-emerald-500">
        <option value="">시·도 선택</option>
        <option v-for="s in sidoList" :key="s" :value="s">{{ s }}</option>
      </select>

      <select v-model="filters.gugun" @change="onGugunChange" :disabled="!gugunList.length"
        class="flex-1 p-2 border rounded-md focus:ring-emerald-500 disabled:opacity-50">
        <option value="">구·군 선택</option>
        <option v-for="g in gugunList" :key="g" :value="g">{{ g }}</option>
      </select>

      <select v-model="filters.dong" @change="onDongChange" :disabled="!dongList.length"
        class="flex-1 p-2 border rounded-md focus:ring-emerald-500 disabled:opacity-50">
        <option value="">읍·면·동 선택</option>
        <option v-for="d in dongList" :key="d" :value="d">{{ d }}</option>
      </select>
    </div>

    <!-- 2. 검색창 + 버튼 -->
    <div class="flex gap-2 mb-4">
      <input v-model="searchQuery" @keyup.enter="onSearch" type="text" placeholder="단지명 검색"
        class="flex-1 p-2 border rounded-md focus:ring-emerald-500" />
      <button @click="onSearch" class="px-4 py-2 bg-emerald-600 text-white rounded-md hover:bg-emerald-700 transition">
        검색
      </button>
    </div>

    <!-- 3. 제어 버튼 -->
    <div class="flex items-center gap-2 mb-4">
      <span v-if="props.noResults" class="text-red-600 mr-auto text-sm"> 검색결과가 없습니다. 필터 초기화를 누르고 다시 검색해주세요!! </span>
      <button @click="showAdvanced = !showAdvanced"
        class="px-3 py-1 border border-emerald-600 text-emerald-600 rounded hover:bg-emerald-50 transition">
        {{ showAdvanced ? '필터 접기' : '상세 필터 보기' }}
      </button>
      <button @click="resetFilters" class="px-3 py-1 bg-gray-200 text-gray-700 rounded hover:bg-gray-300 transition">
        필터 초기화
      </button>
    </div>

    <!-- 4. 상세 필터 -->
    <div v-if="showAdvanced" class="border rounded-md p-4 space-y-4">
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">거래 유형</label>
        <select v-model="filters.dealType" class="w-full p-2 border rounded-md">
          <option value="">전체</option>
          <option value="매매">매매</option>
          <option value="전세">전세</option>
          <option value="월세">월세</option>
        </select>
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">가격 범위 (억원)</label>
        <vue-slider v-model="filters.priceRange" :min="0" :max="100" :interval="1" :dot-size="16"
          :process-style="{ backgroundColor: '#059669' }"
          :tooltip-style="{ backgroundColor: '#059669', borderColor: '#059669' }" />
        <div class="flex justify-between text-sm text-gray-600 mt-1">
          <span>{{ filters.priceRange[0] }}억</span>
          <span>{{ filters.priceRange[1] }}억</span>
        </div>
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">면적</label>
        <select v-model="filters.area" class="w-full p-2 border rounded-md">
          <option value="">전체</option>
          <option value="small">~20평</option>
          <option value="medium">20평~30평</option>
          <option value="large">30평~40평</option>
          <option value="xlarge">40평 이상</option>
        </select>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { ref, reactive, onMounted } from 'vue'
  import axios from 'axios'
  import VueSlider from 'vue-slider-component'
  import 'vue-slider-component/theme/default.css'

  const props = defineProps({
    noResults:Boolean
  })

  // emit 정의
  const emit = defineEmits(['filter-change', 'move-to'])

  const searchQuery = ref('')
  const showAdvanced = ref(false)

  const filters = reactive({
    sido: '',
    gugun: '',
    dong: '',
    dealType: '',
    priceRange: [0, 100],
    area: '',
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
        `https://api.ssafy.blog/api/v1/sidogungu/gugun/${filters.sido}`
      )
      gugunList.value = data
    } else {
      gugunList.value = []
    }
    emit('move-to', { address: buildAddress() })
  }

  // 구·군 변경
  async function onGugunChange() {
    filters.dong = ''
    if (filters.gugun) {
      const { data } = await axios.get(
        `https://api.ssafy.blog/api/v1/sidogungu/dong/${filters.sido}/${filters.gugun}`
      )
      dongList.value = data
    } else {
      dongList.value = []
    }
    emit('move-to', { address: buildAddress() })
  }

  // 읍·면·동 변경
  function onDongChange() {
    emit('move-to', { address: buildAddress() })
  }

  // 검색 실행
  function onSearch() {
    if (!searchQuery.value) {
      console.log('검색어가 없어서 검색을 건너뜁니다.')
      return
    }
    emit('filter-change', {
      partialName: searchQuery.value,
      sido: filters.sido,
      gugun: filters.gugun,
      dong: filters.dong,
      dealType: filters.dealType,
      priceRange: filters.priceRange,
      area: filters.area,
    })
  }

  // 필터 초기화
  function resetFilters() {
    searchQuery.value = ''
    Object.assign(filters, {
      sido: '',
      gugun: '',
      dong: '',
      dealType: '',
      priceRange: [0, 100],
      area: '',
    })
    gugunList.value = []
    dongList.value = []
    emit('filter-change', {})
  }
</script>

<style scoped>
.bg-white {
  background-color: white;
}

.rounded-lg {
  border-radius: 0.5rem;
}
</style>
