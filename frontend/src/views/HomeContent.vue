<!-- src/views/HomeContent.vue -->
<template>
  <main class="container mx-auto px-4 mt-3 flex h-[calc(100vh-6rem)] overflow-hidden gap-4">
    <!-- 사이드바 -->
    <div
      :class="showPropertyDetails ? 'w-1/3 rounded-lg bg-white shadow-lg' : 'w-0'"
      class="transition-[width] duration-300 ease-in-out overflow-hidden"
    >
      <PropertyDetailsSidebar
        :isOpen="showPropertyDetails"
        :property="selectedProperty"
        @close="showPropertyDetails = false"
      />
    </div>

    <!-- 지도 -->
    <section
      :class="showPropertyDetails ? 'w-2/3' : 'w-full'"
      class="transition-all duration-300 ease-in-out flex flex-col rounded-lg overflow-hidden space-y-5"
    >
      <PropertyFilters @filter-change="handleFilterChange" @move-to="handleMoveTo" />
      <div class="flex justify-end mb-2 px-2 space-x-2">
        <button @click="toggleSearchMarkers" class="px-3 py-1 bg-emerald-600 text-white rounded">
          {{ showSearchMarkers ? '검색 마커 숨기기' : '검색 마커 보기' }}
        </button>
      </div>
      <MapComponent
        ref="mapRef"
        :properties="filteredProperties"
        :search-results="searchResults"
        :show-search="showSearchMarkers"
        @select-property="handleSelectProperty"
        class="flex-1"
      />
    </section>

    <!-- 챗봇 -->
    <aside class="flex-shrink-0 w-1/4 overflow-auto shadow-lg">
      <ChatbotInterface class="h-full" @search-houses="onSearchHouses" />
    </aside>
  </main>
</template>

<script setup>
import { ref, computed } from 'vue'
import PropertyFilters from '@/components/PropertyFilters.vue'
import MapComponent from '@/components/MapComponent.vue'
import ChatbotInterface from '@/components/ChatbotInterface.vue'
import PropertyDetailsSidebar from '@/components/PropertyDetailsSidebar.vue'

const mapRef = ref(null)
const searchResults = ref([])
const showSearchMarkers = ref(false)

const showPropertyDetails = ref(false)
const selectedProperty = ref(null)

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

function handleFilterChange(filters) {
  activeFilters.value = filters
}
function handleMoveTo({ address }) {
  mapRef.value.panToAddress(address)
}
function toggleSearchMarkers() {
  showSearchMarkers.value = !showSearchMarkers.value
}
function onSearchHouses(houses) {
  searchResults.value = houses
  showSearchMarkers.value = true
}
function handleSelectProperty(house) {
  selectedProperty.value = {
    title: house.aptNm,
    address: `${house.roadNm} ${house.roadNmBonbun}${house.roadNmBubun}`,
    builtYear: house.buildYear,
    type: house.propertyType || '데이터 없음',
    dealType: house.dealType || '데이터 없음',
    priceValue: house.priceValue,
    sizeValue: house.sizeValue,
    price: house.priceText,
    size: house.sizeText,
    location: house.umdNm,
    roi: house.roi,
    priceChange: house.priceChange,
    image: house.imageUrl,
    description: house.description,
    amenities: house.amenities,
    possibleDay: house.possibleDay,
  }
  showPropertyDetails.value = true
}

// (필요 시) 초기 데이터 로드
// onMounted(async () => { properties.value = await axios.get(...).then(r => r.data) })
</script>
