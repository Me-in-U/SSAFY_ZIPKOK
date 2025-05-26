<template>
  <main
    class="container mx-auto pt-4 pb-2 pl-2 pr-2 flex h-[calc(100vh-7rem)] overflow-hidden gap-3.5"
  >
    <!-- 왼쪽 사이드바 -->
    <div
      :class="showDetailInfo ? 'w-2/5 min-w-[220px] rounded-lg bg-white shadow-4way' : 'w-0'"
      class="transition-[width] duration-300 ease-in-out overflow-hidden"
    >
      <PropertyDetailsSidebar
        v-if="showDetailInfo"
        :aptSeq="selectedAptSeq"
        :is-open="showDetailInfo"
        :is-favorite="userStore.favoriteSeqs.includes(selectedAptSeq)"
        @close="showDetailInfo = false"
        @toggle-favorite="onToggleFavorite"
      />
    </div>

    <!-- 중간영역 -->
    <section
      :class="showDetailInfo ? 'w-4/5' : 'w-full'"
      class="transition-all duration-300 min-w-[320px] ease-in-out flex flex-col rounded-lg overflow-hidden space-y-4 shadow-4way"
    >
      <!-- 필터 지도 버튼 래퍼 -->
      <div class="relative flex-1">
        <!-- 상세 필터 -->
        <div class="absolute top-4 left-1/2 transform -translate-x-1/2 z-10">
          <PropertyFilters
            :no-results="searchResultsFilter.length === 0"
            :has-results="searchResultsFilter.length > 0"
            :searched="hasFilterSearched"
            @search-filter="onSearchFilter"
            @search-reset="onSearchReset"
            @move-to="handleMoveTo"
          />
        </div>
        <MapComponent
          ref="mapRef"
          :properties="filteredProperties"
          :search-results-gpt="searchResultsGpt"
          :search-results-filter="searchResultsFilter"
          :favorite-seqs="userStore.favoriteSeqs"
          :show-base="showBaseMarkers"
          :show-favorite="showFavoriteMarkers"
          :show-search="showSearchMarkers"
          @select-property="handleSelectProperty"
          class="w-full h-full"
        />
        <!-- 지도의 아래 중앙에 버튼 3개 -->
        <div
          class="absolute bottom-4 left-1/2 transform -translate-x-1/2 flex gap-2 bg-white/80 p-2 rounded-full shadow-lg"
        >
          <MarkerButtons
            :show-base="showBaseMarkers"
            :show-favorite="showFavoriteMarkers"
            :show-search="showSearchMarkers"
            :map-type="selectedMapType"
            @toggle-base="showBaseMarkers = !showBaseMarkers"
            @toggle-favorite="showFavoriteMarkers = !showFavoriteMarkers"
            @toggle-search="showSearchMarkers = !showSearchMarkers"
            @toggle-maptype="handleSetMapType"
            @zoom="handleZoom"
          />
        </div>
      </div>
    </section>

    <!-- 오른쪽 챗봇 -->
    <aside
      class="flex-shrink-0 flex flex-col w-1/5 min-w-[180px] overflow-auto rounded-lg shadow-4way"
    >
      <ChatbotInterface class="h-full" @search-gpt="onSearchGpt" />
    </aside>
  </main>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import axios from 'axios'
import MapComponent from '@/components/MapComponent.vue'
import PropertyFilters from '@/components/PropertyFilters.vue'
import ChatbotInterface from '@/components/ChatbotInterface.vue'
import PropertyDetailsSidebar from '@/components/PropertyDetailsSidebar.vue'
import MarkerButtons from '@/components/MarkerButtons.vue'

// 토글 상태
const showBaseMarkers = ref(true)
const showFavoriteMarkers = ref(true)
const showSearchMarkers = ref(false)
const selectedMapType = ref('roadmap')

// 지도 & 검색
const mapRef = ref(null)
const searchResultsGpt = ref([]) // 클라이언트 필터링 후
const searchResultsFilter = ref([]) // 클라이언트 필터링 후

// 전체 매물
const properties = ref([])

// 검색 시도(try)를 추적하는 플래그
const hasFilterSearched = ref(false)

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

function handleSetMapType(type) {
  // MapComponent.defineExpose 로 노출된 메서드 호출
  mapRef.value.setMapType(type)
  selectedMapType.value = type
}

function handleZoom(dir) {
  if (dir === 'in') mapRef.value.zoomIn()
  else mapRef.value.zoomOut()
}

// 지역 필터로 지도 이동
function handleMoveTo({ address }) {
  mapRef.value.panToAddress(address)
}

// 검색결과 마커표시(챗봇)
function onSearchGpt(houses) {
  searchResultsGpt.value = houses
  showSearchMarkers.value = true
}

// 검색결과 마커표시(마커)
function onSearchFilter(houses) {
  console.log('[검색결과]: ', houses)
  searchResultsFilter.value = houses
  hasFilterSearched.value = true
  showSearchMarkers.value = true
}

function onSearchReset() {
  hasFilterSearched.value = false
  searchResultsFilter.value = []
}

// 매물 클릭 → 사이드바 열기
function handleSelectProperty(house) {
  selectedAptSeq.value = house.aptSeq
  showDetailInfo.value = true
  console.log('[선택된 매물]: ', house)
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
