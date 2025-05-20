<template>
  <main class="container mx-auto px-4 mt-3 flex h-[calc(100vh-6rem)] overflow-hidden gap-4">
    <!-- 사이드바 -->
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

    <!-- 지도 -->
    <section
      :class="showDetailInfo ? 'w-4/5' : 'w-full'"
      class="transition-all duration-300 min-w-[320px] ease-in-out flex flex-col rounded-lg overflow-hidden space-y-5"
    >
      <PropertyFilters @filter-change="handleFilterChange" @move-to="handleMoveTo" />

      <!-- Todo: 이동예정 -->
      <!-- 토글 버튼 그룹 -->
      <div class="flex justify-end space-x-2 mb-2 px-2">
        <button
          @click="showBaseMarkers = !showBaseMarkers"
          :class="showBaseMarkers ? 'bg-emerald-600 text-white' : 'bg-gray-200 text-gray-700'"
          class="px-3 py-1 rounded"
        >
          기본 {{ showBaseMarkers ? '숨기기' : '보기' }}
        </button>
        <button
          @click="showFavoriteMarkers = !showFavoriteMarkers"
          :class="showFavoriteMarkers ? 'bg-emerald-600 text-white' : 'bg-gray-200 text-gray-700'"
          class="px-3 py-1 rounded"
        >
          즐겨찾기 {{ showFavoriteMarkers ? '숨기기' : '보기' }}
        </button>
        <button
          @click="showSearchMarkers = !showSearchMarkers"
          :class="showSearchMarkers ? 'bg-emerald-600 text-white' : 'bg-gray-200 text-gray-700'"
          class="px-3 py-1 rounded"
        >
          검색 {{ showSearchMarkers ? '숨기기' : '보기' }}
        </button>
      </div>

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

    <!-- 챗봇 -->
    <aside class="flex-shrink-0 w-1/5 min-w-[180px] overflow-auto shadow-lg">
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

// refs
// 토글 상태
const showBaseMarkers = ref(true)
const showFavoriteMarkers = ref(true)
const showSearchMarkers = ref(false)
// 지도 & 검색
const mapRef = ref(null)
const rawSearchResults = ref([])       // 1 원본 검색 결과를 저장할 변수
const searchResults = ref([])          // 2 지도에 표시할(필터 적용된) 결과
// 전체 매물
const properties = ref([])
const activeFilters = ref({
  propertyType: '',
  dealType: '',
  priceRange: [0, 100],
  area: '',
  builtYear: '',
  household: '',
  availableIn: ''
})
// 사이드바
const showDetailInfo = ref(false)
const selectedAptSeq = ref('')

// userStore
const userStore = useUserStore()

// computed
const filteredProperties = computed(() =>
  properties.value.filter(p => {
    if (activeFilters.value.propertyType && p.type !== activeFilters.value.propertyType) return false
    if (activeFilters.value.dealType && p.dealType !== activeFilters.value.dealType) return false
    const [minP, maxP] = activeFilters.value.priceRange
    if (p.priceValue < minP * 1e8 || p.priceValue > maxP * 1e8) return false
    if (activeFilters.value.area) {
      const minA = p.sizeValue, maxA = p.sizeValue
      switch (activeFilters.value.area) {
        case 'small': if (maxA > 20) return false; break
        case 'medium': if (minA < 20 || maxA > 30) return false; break
        case 'large': if (minA < 30 || maxA > 40) return false; break
        case 'xlarge': if (minA < 40) return false; break
      }
    }
    if (activeFilters.value.builtYear && p.buildYear < Number(activeFilters.value.builtYear)) return false
    return true
  })
)
//지역 필터로 이동
function handleMoveTo({ address }) {
  mapRef.value.panToAddress(address)
}

// activeFilters 변경 시 rawSearchResults 기반 상세 필터 적용
watch(activeFilters, () => {
  if (rawSearchResults.value.length) applyDetailedFilters()
}, { deep: true })

// 필터/검색 처리
async function handleFilterChange({
  searchQuery,
  sido,
  gugun,
  dong,
  propertyType,
  dealType,
  priceRange,
  area,
  builtYear,
  household,
  availableIn
}) {
  activeFilters.value = { propertyType, dealType, priceRange, area, builtYear, household, availableIn }

  try {
    const [minPrice, maxPrice] = priceRange
    const { data: houses } = await axios.get('http://localhost:8080/api/v1/house/search/full', {
      params: {
        partialName: searchQuery,
        sido,
        gugun,
        dong,
        propertyType,
        dealType,
        builtYear,
        minPrice: minPrice * 1e8,
        maxPrice: maxPrice * 1e8,
        areaOption: area
      }
    })
    rawSearchResults.value = Array.isArray(houses) ? houses : []
    applyDetailedFilters()
    showSearchMarkers.value = true
    showBaseMarkers.value = false
    console.log('🔍 백엔드 응답:', houses)
    // 첫 번째 매물의 위도/경도로 이동
    if (rawSearchResults.value.length > 0) {
      const first = rawSearchResults.value[0]
      mapRef.value.panToCoords({
        latitude: first.latitude,
        longitude: first.longitude
      })
    }
  } catch (e) {
    console.error('검색(full) 오류', e)
    rawSearchResults.value = []
    searchResults.value = []
    showSearchMarkers.value = false
  }
}

// 상세 필터 적용 후 지도 표시
function applyDetailedFilters() {
  let filtered = rawSearchResults.value
  if (activeFilters.value.propertyType) filtered = filtered.filter(h => h.propertyType === activeFilters.value.propertyType)
  if (activeFilters.value.dealType) filtered = filtered.filter(h => h.dealType === activeFilters.value.dealType)
  const [minP, maxP] = activeFilters.value.priceRange
  filtered = filtered.filter(h => (h.latestPrice >= minP * 1e8 && h.latestPrice <= maxP * 1e8))
  if (activeFilters.value.area) {
    switch (activeFilters.value.area) {
      case 'small': filtered = filtered.filter(h => h.areaMax <= 20); break
      case 'medium': filtered = filtered.filter(h => h.areaMin >= 20 && h.areaMax <= 30); break
      case 'large': filtered = filtered.filter(h => h.areaMin >= 30 && h.areaMax <= 40); break
      case 'xlarge': filtered = filtered.filter(h => h.areaMin >= 40); break
    }
  }
  if (activeFilters.value.builtYear) filtered = filtered.filter(h => h.buildYear >= Number(activeFilters.value.builtYear))

  searchResults.value = filtered
  showSearchMarkers.value = filtered.length > 0
}
// 챗봇 이벤트
function onSearchHouses(houses) {
  searchResults.value = houses
  showSearchMarkers.value = true
}

// 매물 클릭 → aptSeq 설정 & 사이드바 열기
function handleSelectProperty(house) {
  selectedAptSeq.value = house.aptSeq
  showDetailInfo.value = true
  console.log('🏠 선택된 aptSeq:', house.aptSeq)
}

// 즐겨찾기 등록/해제
async function onToggleFavorite(aptSeq) {
  const mno = userStore.profile.mno
  if (userStore.favoriteSeqs.includes(aptSeq)) {
    await axios.delete(`/api/v1/members/${mno}/favorites/${aptSeq}`)
    userStore.favoriteSeqs = userStore.favoriteSeqs.filter((seq) => seq !== aptSeq)
  } else {
    await axios.post(`/api/v1/members/${mno}/favorites/${aptSeq}`)
    userStore.favoriteSeqs.push(aptSeq)
  }
}
</script>
