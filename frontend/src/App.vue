<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 헤더 -->
    <header class="bg-white shadow-sm">
      <div class="container mx-auto px-4 py-3 flex items-center justify-between">
        <div class="flex items-center">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="text-emerald-600 mr-2"
          >
            <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
            <polyline points="9 22 9 12 15 12 15 22" />
          </svg>
          <h1 class="text-xl font-bold text-gray-800">부동산 투자 AI</h1>
        </div>
        <nav class="hidden md:flex items-center space-x-6">
          <a href="#" class="text-gray-600 hover:text-emerald-600 font-medium">홈</a>
          <a href="#" class="text-gray-600 hover:text-emerald-600 font-medium">투자 분석</a>
          <a
            href="#"
            class="text-gray-600 hover:text-emerald-600 font-medium"
            @click.prevent="showRecommendedModal = true"
            >추천 매물</a
          >
          <a href="#" class="text-gray-600 hover:text-emerald-600 font-medium">시장 동향</a>
          <a href="#" class="text-gray-600 hover:text-emerald-600 font-medium">내 계정</a>
        </nav>
        <button class="md:hidden" @click="mobileMenuOpen = !mobileMenuOpen">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <line x1="4" x2="20" y1="12" y2="12" />
            <line x1="4" x2="20" y1="6" y2="6" />
            <line x1="4" x2="20" y1="18" y2="18" />
          </svg>
        </button>
      </div>
      <!-- 모바일 메뉴 -->
      <div v-if="mobileMenuOpen" class="md:hidden bg-white border-t">
        <div class="container mx-auto px-4 py-2 space-y-2">
          <a href="#" class="block py-2 text-gray-600 hover:text-emerald-600 font-medium">홈</a>
          <a href="#" class="block py-2 text-gray-600 hover:text-emerald-600 font-medium"
            >투자 분석</a
          >
          <a
            href="#"
            class="block py-2 text-gray-600 hover:text-emerald-600 font-medium"
            @click.prevent="showRecommendedModal = true"
            >추천 매물</a
          >
          <a href="#" class="block py-2 text-gray-600 hover:text-emerald-600 font-medium"
            >시장 동향</a
          >
          <a href="#" class="block py-2 text-gray-600 hover:text-emerald-600 font-medium"
            >내 계정</a
          >
        </div>
      </div>
    </header>

    <!-- 메인 콘텐츠 -->
    <main class="container mx-auto px-4 py-6">
      <div class="flex flex-col lg:flex-row gap-6">
        <!-- 왼쪽: 지도와 UI 요소 (3/4 너비) -->
        <div class="w-full lg:w-3/4 space-y-6">
          <!-- 검색 및 필터 -->
          <PropertyFilters @filter-change="handleFilterChange" />

          <!-- 지도 (더 크게 표시) -->
          <MapComponent
            :properties="filteredProperties"
            @select-property="handleSelectProperty"
            class="h-[calc(100vh-12rem)]"
          />
        </div>

        <!-- 오른쪽: 챗봇 (1/4 너비) -->
        <div class="w-full lg:w-1/4 h-[calc(100vh-6rem)] flex flex-col">
          <ChatbotInterface class="h-full" />
        </div>
      </div>
    </main>

    <!-- 추천 매물 모달 -->
    <RecommendedPropertiesModal
      :show="showRecommendedModal"
      :properties="properties"
      @close="showRecommendedModal = false"
    />

    <!-- 속성 세부 정보 사이드바 (왼쪽에서 애니메이션으로 나타남) -->
    <PropertyDetailsSidebar
      :property="selectedProperty"
      :is-open="showPropertyDetails"
      @close="showPropertyDetails = false"
    />
  </div>
</template>

<script setup>
import './assets/tailwind.css' // Corrected the relative path to the Tailwind CSS file
import { ref, computed } from 'vue'
import PropertyFilters from './components/PropertyFilters.vue'
import MapComponent from './components/MapComponent.vue'
import ChatbotInterface from './components/ChatbotInterface.vue'
import RecommendedPropertiesModal from './components/RecommendedPropertiesModal.vue'
import PropertyDetailsSidebar from './components/PropertyDetailsSidebar.vue'

// 모바일 메뉴 상태
const mobileMenuOpen = ref(false)

// 모달 및 사이드바 상태
const showRecommendedModal = ref(false)
const showPropertyDetails = ref(false)
const selectedProperty = ref(null)

// 필터 상태
const activeFilters = ref({
  propertyType: '',
  priceRange: [0, 100],
  area: '',
  dealType: '',
})

// 샘플 부동산 데이터 (JSON)
const properties = ref([
  {
    id: 1,
    title: '강남 래미안 아파트',
    type: '아파트',
    dealType: '매매',
    price: '12억 5,000만원',
    priceValue: 1250000000,
    size: '32평',
    sizeValue: 32,
    location: '서울시 강남구',
    address: '서울시 강남구 테헤란로 152',
    roi: '4.2%',
    priceChange: '+2.1%',
    image: '/placeholder.svg?height=150&width=250',
    lat: 37.5,
    lng: 127.0,
    description:
      '강남 중심부에 위치한 프리미엄 아파트. 교통과 편의시설이 우수하며 투자 가치가 높습니다.',
    amenities: ['헬스장', '수영장', '주차장', '놀이터', '24시간 경비'],
    builtYear: 2018,
  },
  {
    id: 2,
    title: '판교 알파리움 오피스텔',
    type: '오피스텔',
    dealType: '전세',
    price: '7억 8,000만원',
    priceValue: 780000000,
    size: '24평',
    sizeValue: 24,
    location: '경기도 성남시 분당구',
    address: '경기도 성남시 분당구 판교역로 235',
    roi: '5.1%',
    priceChange: '+1.8%',
    image: '/placeholder.svg?height=150&width=250',
    lat: 37.4,
    lng: 127.1,
    description: '판교 테크노밸리 인근 신축 오피스텔. IT 기업 밀집 지역으로 임대 수요가 많습니다.',
    amenities: ['헬스장', '카페', '주차장', '편의점', '보안시스템'],
    builtYear: 2020,
  },
  {
    id: 3,
    title: '송파 헬리오시티',
    type: '아파트',
    dealType: '매매',
    price: '15억 2,000만원',
    priceValue: 1520000000,
    size: '39평',
    sizeValue: 39,
    location: '서울시 송파구',
    address: '서울시 송파구 올림픽로 300',
    roi: '3.8%',
    priceChange: '+3.2%',
    image: '/placeholder.svg?height=150&width=250',
    lat: 37.5,
    lng: 127.1,
    description: '송파구 최대 규모 단지로 편의시설이 잘 갖춰져 있으며 교통이 편리합니다.',
    amenities: ['헬스장', '수영장', '도서관', '상가', '공원'],
    builtYear: 2019,
  },
  {
    id: 4,
    title: '용산 더 센트럴',
    type: '아파트',
    dealType: '매매',
    price: '18억 3,000만원',
    priceValue: 1830000000,
    size: '42평',
    sizeValue: 42,
    location: '서울시 용산구',
    address: '서울시 용산구 한강대로 100',
    roi: '3.5%',
    priceChange: '+2.8%',
    image: '/placeholder.svg?height=150&width=250',
    lat: 37.52,
    lng: 126.98,
    description: '용산역 인근 프리미엄 주거단지. 한강 조망권과 교통 편의성이 뛰어납니다.',
    amenities: ['피트니스센터', '스파', '라운지', '키즈룸', '스카이라운지'],
    builtYear: 2021,
  },
  {
    id: 5,
    title: '광교 컨벤션 오피스텔',
    type: '오피스텔',
    dealType: '월세',
    price: '5억 9,000만원 (월 250만원)',
    priceValue: 590000000,
    size: '20평',
    sizeValue: 20,
    location: '경기도 수원시 영통구',
    address: '경기도 수원시 영통구 광교중앙로 145',
    roi: '5.5%',
    priceChange: '+1.5%',
    image: '/placeholder.svg?height=150&width=250',
    lat: 37.3,
    lng: 127.05,
    description: '광교신도시 중심부 위치, 대학교와 기업체가 인접해 있어 임대 수요가 많습니다.',
    amenities: ['헬스장', '세탁실', '스터디룸', '주차장', '무인택배함'],
    builtYear: 2017,
  },
  {
    id: 6,
    title: '동탄 메타폴리스',
    type: '아파트',
    dealType: '매매',
    price: '9억 2,000만원',
    priceValue: 920000000,
    size: '34평',
    sizeValue: 34,
    location: '경기도 화성시 동탄',
    address: '경기도 화성시 동탄대로 123',
    roi: '4.7%',
    priceChange: '+2.3%',
    image: '/placeholder.svg?height=150&width=250',
    lat: 37.2,
    lng: 127.07,
    description:
      '동탄 신도시 핵심 입지에 위치한 아파트. 교육 환경이 우수하고 신도시 인프라가 잘 갖춰져 있습니다.',
    amenities: ['헬스장', '수영장', '도서관', '놀이터', '주민센터'],
    builtYear: 2016,
  },
])

// 필터링된 속성 계산
const filteredProperties = computed(() => {
  return properties.value.filter((property) => {
    // 속성 유형 필터
    if (activeFilters.value.propertyType && property.type !== activeFilters.value.propertyType) {
      return false
    }

    // 가격 범위 필터
    const minPrice = activeFilters.value.priceRange[0] * 100000000 // 억 단위로 변환
    const maxPrice = activeFilters.value.priceRange[1] * 100000000
    if (property.priceValue < minPrice || property.priceValue > maxPrice) {
      return false
    }

    // 면적 필터
    if (activeFilters.value.area) {
      const [min, max] = getAreaRange(activeFilters.value.area)
      if (property.sizeValue < min || property.sizeValue > max) {
        return false
      }
    }

    // 거래 유형 필터
    if (activeFilters.value.dealType && property.dealType !== activeFilters.value.dealType) {
      return false
    }

    return true
  })
})

// 면적 범위 가져오기 헬퍼 함수
function getAreaRange(areaOption) {
  switch (areaOption) {
    case 'small':
      return [0, 20]
    case 'medium':
      return [20, 30]
    case 'large':
      return [30, 40]
    case 'xlarge':
      return [40, 100]
    default:
      return [0, 100]
  }
}

// 이벤트 핸들러
function handleFilterChange(filters) {
  activeFilters.value = { ...filters }
}

function handleSelectProperty(property) {
  selectedProperty.value = property
  showPropertyDetails.value = true
}
</script>
