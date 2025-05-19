<!-- src/views/HomeContent.vue -->
<template>
  <main class="container mx-auto px-4 mt-3 flex h-[calc(100vh-6rem)] overflow-hidden gap-4">
    <!-- 사이드바 -->
    <div
      :class="showDetailInfo ? 'w-2/5 rounded-lg bg-white shadow-lg' : 'w-0'"
      class="transition-[width] duration-300 ease-in-out overflow-hidden"
    >
      <PropertyDetailsSidebar
        v-if="showDetailInfo"
        :aptSeq="selectedAptSeq"
        :isOpen="showDetailInfo"
        :is-favorite="favoriteSeqs.includes(selectedAptSeq)"
        @close="showDetailInfo = false"
        @toggle-favorite="onToggleFavorite"
        @consult="onConsult"
      />
    </div>

    <!-- 지도 -->
    <section
      :class="showDetailInfo ? 'w-4/5' : 'w-full'"
      class="transition-all duration-300 ease-in-out flex flex-col rounded-lg overflow-hidden space-y-5"
    >
      <PropertyFilters @filter-change="handleFilterChange" @move-to="handleMoveTo" />

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
        :favorite-seqs="favoriteSeqs"
        :show-base="showBaseMarkers"
        :show-favorite="showFavoriteMarkers"
        :show-search="showSearchMarkers"
        @select-property="handleSelectProperty"
        class="flex-1"
      />
    </section>

    <!-- 챗봇 -->
    <aside class="flex-shrink-0 w-1/5 overflow-auto shadow-lg">
      <ChatbotInterface class="h-full" @search-houses="onSearchHouses" />
    </aside>
  </main>
</template>

<script setup>
import { ref, computed, inject } from 'vue'
import axios from 'axios'
import PropertyFilters from '@/components/PropertyFilters.vue'
import MapComponent from '@/components/MapComponent.vue'
import ChatbotInterface from '@/components/ChatbotInterface.vue'
import PropertyDetailsSidebar from '@/components/PropertyDetailsSidebar.vue'

// 토글 상태
const showBaseMarkers = ref(true)
const showFavoriteMarkers = ref(true)
const showSearchMarkers = ref(false)

// 전역
const user = inject('user')
const favoriteSeqs = inject('favoriteSeqs')

// 지도 & 검색
const mapRef = ref(null)
const searchResults = ref([])

// 사이드바
const showDetailInfo = ref(false)
const selectedAptSeq = ref('') // string

// 전체 매물 + 필터
const properties = ref([])
const activeFilters = ref({ propertyType: '', priceRange: [0, 100], area: '', dealType: '' })
const filteredProperties = computed(() =>
  properties.value.filter((p) => {
    if (activeFilters.value.propertyType && p.type !== activeFilters.value.propertyType)
      return false
    if (activeFilters.value.area) {
      const [minA, maxA] = activeFilters.value.area.split('-').map(Number)
      if (p.sizeValue < minA || p.sizeValue > maxA) return false
    }
    const [minP, maxP] = activeFilters.value.priceRange
    if (p.priceValue < minP * 1e8 || p.priceValue > maxP * 1e8) return false
    if (activeFilters.value.dealType && p.dealType !== activeFilters.value.dealType) return false
    return true
  }),
)

// 필터/이동
function handleFilterChange(filters) {
  activeFilters.value = filters
}
function handleMoveTo({ address }) {
  mapRef.value.panToAddress(address)
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
  const mno = user.value.mno
  if (favoriteSeqs.value.includes(aptSeq)) {
    await axios.delete(`http://localhost:8080/api/v1/members/${mno}/favorites/${aptSeq}`)
    favoriteSeqs.value = favoriteSeqs.value.filter((seq) => seq !== aptSeq)
  } else {
    await axios.post(`http://localhost:8080/api/v1/members/${mno}/favorites/${aptSeq}`)
    favoriteSeqs.value.push(aptSeq)
  }
}

// 상담 신청
function onConsult(aptSeq) {
  console.log('📨 상담 신청:', aptSeq)
}
</script>
