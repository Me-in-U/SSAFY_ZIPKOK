<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 헤더 -->
    <header class="bg-white shadow-sm">
      <div class="container mx-auto px-4 py-3 flex items-center justify-between">
        <!-- 로고 -->
        <div class="flex items-center">
          <img src="/favicon.ico" alt="Logo" class="w-6 h-6 mr-2" />
          <h1 class="text-xl font-bold text-gray-800"><a href="/">집콕(ZIPKOK)</a></h1>
        </div>
        <!-- 네비게이션 -->
        <nav class="hidden md:flex items-center space-x-6">
          <!-- 공통 메뉴 -->
          <!-- <router-link to="/" class="text-gray-600 hover:text-emerald-600 font-medium">
            메인화면
          </router-link> -->
          <a href="#" class="text-gray-600 hover:text-emerald-600 font-medium">투자 분석</a>
          <a href="#" class="text-gray-600 hover:text-emerald-600 font-medium"
            @click.prevent="openRecommendedModal">추천 매물</a>
          <a href="#" class="text-gray-600 hover:text-emerald-600 font-medium">시장 동향</a>
          <!-- 인증 상태 분기 -->
          <template v-if="!user">
            <!-- 로그아웃 상태 -->
            <button class="text-gray-600 hover:text-emerald-600 font-medium" @click="showLoginModal = true">
              로그인
            </button>
            <router-link to="/member/mvRegist" class="text-gray-600 hover:text-emerald-600 font-medium">
              회원가입
            </router-link>
          </template>
          <template v-else>
            <!-- 로그인 상태 -->
            <span class="text-gray-800 font-medium"> {{ user.name }}님 </span>
            <router-link to="/" class="text-gray-600 hover:text-emerald-600 font-medium">
              마이페이지
            </router-link>
            <button class="text-gray-600 hover:text-emerald-600 font-medium" @click="handleLogout">
              로그아웃
            </button>
          </template>
        </nav>
        <button class="md:hidden" @click="mobileMenuOpen = !mobileMenuOpen">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="4" x2="20" y1="12" y2="12" />
            <line x1="4" x2="20" y1="6" y2="6" />
            <line x1="4" x2="20" y1="18" y2="18" />
          </svg>
        </button>
      </div>
      <!-- 모바일 메뉴 -->
      <div v-if="mobileMenuOpen" class="md:hidden bg-white border-t">
        <div class="container mx-auto px-4 py-2 space-y-2">
          <!-- 추천 매물, 시장 동향 -->
          <button class="block w-full text-left py-2 text-gray-600 hover:text-emerald-600 font-medium"
            @click.prevent="openRecommendedModal">
            추천 매물
          </button>
          <a href="#" class="block py-2 text-gray-600 hover:text-emerald-600 font-medium">
            시장 동향
          </a>

          <!-- 인증 상태 분기 -->
          <template v-if="!user">
            <button class="block w-full text-left py-2 text-gray-600 hover:text-emerald-600 font-medium"
              @click="showLoginModal = true">
              로그인
            </button>
            <router-link to="/member/mvRegist" class="block py-2 text-gray-600 hover:text-emerald-600 font-medium">
              회원가입
            </router-link>
          </template>
          <template v-else>
            <router-link to="/member/mypage" class="block py-2 text-gray-600 hover:text-emerald-600 font-medium">
              마이페이지
            </router-link>
            <button class="block w-full text-left py-2 text-gray-600 hover:text-emerald-600 font-medium"
              @click="handleLogout">
              로그아웃
            </button>
          </template>
        </div>
      </div>
    </header>

    <!-- 메인 영역: 화면 전체 너비 사용 -->
    <main class="container mx-auto px-4 mt-3 flex h-[calc(100vh-6rem)] overflow-hidden gap-4">
      <!-- 사이드바 (조건부 = w-1/3, 숨김이면 w-0) -->
      <div :class="showPropertyDetails ? 'w-1/3 rounded-lg bg-white shadow-lg' : 'w-0'"
        class="transition-[width] duration-300 ease-in-out overflow-hidden">
        <PropertyDetailsSidebar v-if="showPropertyDetails" :property="selectedProperty" :isOpen="showPropertyDetails"
          @close="showPropertyDetails = false" />
      </div>

      <!-- 지도 (사이드바 열리면 w-2/3, 아니면 w-full) -->
      <section :class="showPropertyDetails ? 'w-2/3' : 'w-full'"
        class="transition-all duration-300 ease-in-out flex flex-col rounded-lg overflow-hidden space-y-5">
        <PropertyFilters @filter-change="handleFilterChange" />
        <MapComponent :properties="filteredProperties" @select-property="handleSelectProperty" class="flex-1" />
      </section>

      <!-- 3) 오른쪽 챗봇 -->
      <aside class="flex-shrink-0 w-1/4 overflow-auto shadow-lg">
        <ChatbotInterface class="h-full" />
      </aside>
    </main>

    <!-- 추천 매물 모달 -->
    
    <RecommendedPropertiesModal  
    :show="showRecommendedModal"  
    :recentProperties="recentProperties" 
      :mostProperties="mostProperties"  
      :recommendedProperties="recommendedProperties" 
      @close="showRecommendedModal = false"  />

    <!-- 로그인 모달 -->
    <div v-if="showLoginModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center overflow-hidden z-50">
      <div class="rounded-lg shadow-lg w-full max-w-md max-h-[160vh] overflow-y-auto">
        <LoginForm @close="showLoginModal = false" @login-success="onLoginSuccess"
          @open-register="((showLoginModal = false), (showRegisterModal = true))" />
      </div>
    </div>
    <!-- 회원가입 모달 -->
    <div v-if="showRegisterModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center overflow-hidden z-50">
      <div class="bg-black rounded-lg shadow-lg w-full max-w-md max-h-[160vh] overflow-y-auto">
        <RegisterForm @close="showRegisterModal = false" />
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios'
import './assets/tailwind.css' // Corrected the relative path to the Tailwind CSS file
import { ref, computed, onMounted } from 'vue'
import PropertyFilters from './components/PropertyFilters.vue'
import MapComponent from './components/MapComponent.vue'
import ChatbotInterface from './components/ChatbotInterface.vue'
import RecommendedPropertiesModal from './components/RecommendedPropertiesModal.vue'
import PropertyDetailsSidebar from './components/PropertyDetailsSidebar.vue'
import LoginForm from './components/LoginForm.vue'
import RegisterForm from './components/RegisterForm.vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 인증 관련 상태
const user = ref(null)

// 모달 및 사이드바 상태
const showRecommendedModal = ref(false)
const showLoginModal = ref(false)
const showRegisterModal = ref(false)
const showPropertyDetails = ref(false)
const selectedProperty = ref(null)
const mobileMenuOpen = ref(false)

// 필터 상태
const activeFilters = ref({
  propertyType: '',
  priceRange: [0, 100],
  area: '',
  dealType: '',
})

// **추천 매물용 상태 추가**
const recentProperties = ref([])
const mostProperties = ref([])
const recommendedProperties = ref([])
const properties = ref([])
// 추천 매물 버튼 클릭 시 백엔드 호출
async function openRecommendedModal() {
  // 이미 데이터를 가져온 적이 있으면 바로 모달만 열어주고 리턴
  if (recentProperties.value.length > 0) {
    showRecommendedModal.value = true
    return
  }
  try {
    const [r, m, c] = await Promise.all([
      axios.get('http://localhost:8080/api/v1/house/recommend/recent?limit=6'),
      axios.get('http://localhost:8080/api/v1/house/recommend/most?limit=6'),
      axios.get('http://localhost:8080/api/v1/house/recommend/composite?limit=6'),
    ])
    recentProperties.value = r.data
    mostProperties.value = m.data
    recommendedProperties.value = c.data
      // → 여기서 찍어보면 App.vue 쪽 상태가 정확히 무엇인지 알 수 있습니다.
      console.log('[App.vue] recentProperties', JSON.stringify(recentProperties.value, null, 2))
      console.log('[App.vue] mostProperties', JSON.stringify(mostProperties.value, null, 2))
      console.log('[App.vue] recommendedProperties', JSON.stringify(recommendedProperties.value, null, 2))

    showRecommendedModal.value = true
  } catch (err) {
    console.error('추천 매물 불러오기 실패', err)
  }
}
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

// 페이지 로드 시 토큰 있으면 사용자 정보 가져오기
onMounted(async () => {
  const token = localStorage.getItem('jwtToken')
  if (!token) return

  try {
    const res = await fetch('https://api.ssafy.blog/api/v1/members/me', {
      headers: {
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + token,
      },
    })
    if (!res.ok) throw new Error('인증 실패')
    const json = await res.json()
    user.value = json.user
  } catch (err) {
    console.warn('토큰 검증 실패, 로그아웃 처리:', err)
    localStorage.removeItem('jwtToken')
  }
})
function onLoginSuccess(loggedUser) {
  user.value = loggedUser
  showLoginModal.value = false
}
// 로그아웃
function handleLogout() {
  localStorage.removeItem('jwtToken')
  // 페이지 리로드 또는 라우터 이동
  router.push('/')
  user.value = null
}

// 이벤트 핸들러
function handleFilterChange(filters) {
  activeFilters.value = { ...filters }
}

function handleSelectProperty(house) {
  selectedProperty.value = {
    // 사이드바가 <h3>{{ property.title }}</h3> 를 그리니까
    title: house.aptNm,

    // 사이드바에서 주소를 {{ property.address }} 로 기대하니
    address: `${house.roadNm} ${house.roadNmBonbun}${house.roadNmBubun}`,

    // 사이드바의 "준공년도"
    builtYear: house.buildYear,

    // 나머지 사이드바 컴포넌트에서 사용되는 필드들
    type: house.propertyType || '데이터 없음', // API에 없으면 기본값
    dealType: house.dealType || '데이터 없음', // 월세/전세/매매 정보가 API에 있으면
    price: house.priceText || '데이터 없음',
    priceValue: house.priceValue || 0,
    size: house.sizeText || '데이터 없음',
    sizeValue: house.sizeValue || 0,
    location: house.umdNm,
    roi: house.roi || '데이터 없음',
    priceChange: house.priceChange || '데이터 없음',
    image: house.imageUrl || '/placeholder.svg',
    description: house.description || '',
    amenities: house.amenities || [],
    possibleDay: house.possibleDay || '데이터 없음',
  }

  showPropertyDetails.value = true
}
</script>
