<template>
  <main class="container mx-auto mt-3 pb-3 flex h-[calc(100vh-6rem)] overflow-hidden gap-3.5">
    <!-- 왼쪽 사이드바 -->
    <div
      :class="showDetailInfo ? 'w-2/5 min-w-[220px] rounded-lg bg-white shadow-lg' : 'w-0'"
      class="transition-[width] duration-300 ease-in-out overflow-hidden"
    >
      <PropertyDetailsSidebar
        v-if="showDetailInfo"
        :aptSeq="selectedAptSeq"
        :isOpen="showDetailInfo"
        :is-favorite="userStore.favoriteSeqs.includes(selectedAptSeq)"
        @close="showDetailInfo = false"
        @toggle-favorite="onToggleFavorite"
      />
    </div>

    <!-- 중간영역 -->
    <section
      :class="showDetailInfo ? 'w-4/5' : 'w-full'"
      class="transition-all duration-300 min-w-[320px] ease-in-out flex flex-col rounded-lg overflow-hidden space-y-4"
    >
      <!-- 상세 필터 -->
      <PropertyFilters
        :no-results="!isLoading && searchResults.length === 0 && hasSearched"
        :show-base="showBaseMarkers"
        :show-favorite="showFavoriteMarkers"
        :show-search="showSearchMarkers"
        @filter-change="handleFilterChange"
        @move-to="handleMoveTo"
        @toggle-base="showBaseMarkers = !showBaseMarkers"
        @toggle-favorite="showFavoriteMarkers = !showFavoriteMarkers"
        @toggle-search="showSearchMarkers = !showSearchMarkers"
      />
      <!-- 지도 -->
      <MapComponent
        ref="mapRef"
        :properties="filteredProperties"
        :search-results="searchResults"
        :favorite-seqs="userStore.favoriteSeqs"
        :show-base="showBaseMarkers"
        :show-favorite="showFavoriteMarkers"
        :show-search="showSearchMarkers"
        @select-property="handleSelectProperty"
        class="flex-1"
      />
    </section>

    <!-- 오른쪽 챗봇 -->
    <aside
      class="flex-shrink-0 flex flex-col w-1/5 min-w-[180px] overflow-auto shadow-lg rounded-lg"
    >
      <ChatbotInterface class="h-full" @search-houses="onSearchHouses" />
    </aside>
  </main>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useUserStore } from '@/stores/user'
import axios from 'axios'
import MapComponent from '@/components/MapComponent.vue'
import PropertyFilters from '@/components/PropertyFilters.vue'
import ChatbotInterface from '@/components/ChatbotInterface.vue'
import PropertyDetailsSidebar from '@/components/PropertyDetailsSidebar.vue'

// 토글 상태
const showBaseMarkers = ref(true)
const showFavoriteMarkers = ref(true)
const showSearchMarkers = ref(false)

// 지도 & 검색
const mapRef = ref(null)
const rawSearchResults = ref([]) // 백엔드 리턴 그대로
const searchResults = ref([]) // 클라이언트 필터링 후

// 검색 실행 여부 트래킹
const hasSearched = ref(false)
const isLoading = ref(false)
// 전체 매물
const properties = ref([])

// client-side 상세 필터 상태
const activeFilters = ref({
  propertyType: '',
  dealType: '',
  priceRange: [0, 100],
  area: '',
  builtYear: '',
  household: '',
  availableIn: '',
})

// 사이드바
const showDetailInfo = ref(false)
const selectedAptSeq = ref('')

// userStore
const userStore = useUserStore()

// 지도용 computed
const filteredProperties = computed(() =>
  properties.value.filter((p) => {
    // (기존 로직 유지)
    if (activeFilters.value.propertyType && p.type !== activeFilters.value.propertyType)
      return false
    if (activeFilters.value.dealType && p.dealType !== activeFilters.value.dealType) return false
    const [minP, maxP] = activeFilters.value.priceRange
    if (p.priceValue < minP * 1e8 || p.priceValue > maxP * 1e8) return false
    if (activeFilters.value.area) {
      const minA = p.sizeValue,
        maxA = p.sizeValue
      switch (activeFilters.value.area) {
        case 'small':
          if (maxA > 20) return false
          break
        case 'medium':
          if (minA < 20 || maxA > 30) return false
          break
        case 'large':
          if (minA < 30 || maxA > 40) return false
          break
        case 'xlarge':
          if (minA < 40) return false
          break
      }
    }
    if (activeFilters.value.builtYear && p.buildYear < Number(activeFilters.value.builtYear))
      return false
    return true
  }),
)

// 지역 필터로 지도 이동
function handleMoveTo({ address }) {
  mapRef.value.panToAddress(address)
}

// activeFilters 변경 시 rawSearchResults 기반 상세 필터 재적용
watch(
  activeFilters,
  () => {
    if (rawSearchResults.value.length) applyDetailedFilters()
  },
  { deep: true },
)

async function handleFilterChange(filters) {
  const q = filters.partialName?.trim()
  const hasRegion = Boolean(filters.sido || filters.gugun || filters.dong)

  // 1) 이름도, 지역도 없으면 초기화
  if (!q && !hasRegion) {
    searchResults.value = []
    showSearchMarkers.value = false
    showBaseMarkers.value = true
    hasSearched.value = false
    return
  }
  hasSearched.value = true
  isLoading.value = true
  // 2) 서버에 보낼 파라미터
  const params = { partialName: q }
  if (filters.sido) params.sido = filters.sido
  if (filters.gugun) params.gugun = filters.gugun
  if (filters.dong) params.dong = filters.dong

  // 3) 마커 전용 API 호출
  let list = []
  try {
    const res = await axios.get('https://api.ssafy.blog/api/v1/house/search/markers', { params })
    list = Array.isArray(res.data) ? res.data : []
  } catch (e) {
    console.error('검색(markers) 오류', e)
  } finally {
    isLoading.value = false
  }

  // 4) 결과 반영
  searchResults.value = list
  showSearchMarkers.value = list.length > 0
  showBaseMarkers.value = !showSearchMarkers.value

  // 5) 첫 위치로 맵 이동
  if (list.length) {
    mapRef.value.panToCoords({
      latitude: list[0].latitude,
      longitude: list[0].longitude,
    })
  }
}

// client-side 상세 필터 함수
function applyDetailedFilters() {
  let filtered = rawSearchResults.value
  if (activeFilters.value.propertyType)
    filtered = filtered.filter((h) => h.propertyType === activeFilters.value.propertyType)
  if (activeFilters.value.dealType)
    filtered = filtered.filter((h) => h.dealType === activeFilters.value.dealType)

  const [minP, maxP] = activeFilters.value.priceRange
  filtered = filtered.filter((h) => h.latestPrice >= minP * 1e8 && h.latestPrice <= maxP * 1e8)

  if (activeFilters.value.area) {
    switch (activeFilters.value.area) {
      case 'small':
        filtered = filtered.filter((h) => h.areaMax <= 20)
        break
      case 'medium':
        filtered = filtered.filter((h) => h.areaMin >= 20 && h.areaMax <= 30)
        break
      case 'large':
        filtered = filtered.filter((h) => h.areaMin >= 30 && h.areaMax <= 40)
        break
      case 'xlarge':
        filtered = filtered.filter((h) => h.areaMin >= 40)
        break
    }
  }
  if (activeFilters.value.builtYear)
    filtered = filtered.filter((h) => h.buildYear >= Number(activeFilters.value.builtYear))

  searchResults.value = filtered
  showSearchMarkers.value = filtered.length > 0
}

// 챗봇 연동
function onSearchHouses(houses) {
  searchResults.value = houses
  showSearchMarkers.value = true
}

// 매물 클릭 → 사이드바 열기
function handleSelectProperty(house) {
  selectedAptSeq.value = house.aptSeq
  showDetailInfo.value = true
}

// 즐겨찾기 토글
async function onToggleFavorite(aptSeq) {
  console.log('[유저 정보]: ', userStore.profile)
  const mno = userStore.profile.mno
  if (userStore.favoriteSeqs.includes(aptSeq)) {
    await axios.delete(`https://api.ssafy.blog/api/v1/members/${mno}/favorites/${aptSeq}`)
    userStore.favoriteSeqs = userStore.favoriteSeqs.filter((s) => s !== aptSeq)
  } else {
    await axios.post(`https://api.ssafy.blog/api/v1/members/${mno}/favorites/${aptSeq}`)
    userStore.favoriteSeqs.push(aptSeq)
  }
}
</script>
