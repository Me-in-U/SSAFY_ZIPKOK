<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-50">
    <div
      class="bg-white rounded-xl shadow-2xl w-full max-w-5xl max-h-[90vh] overflow-hidden relative flex flex-col"
    >
      <!-- 헤더 -->
      <div class="relative">
        <div
          class="absolute inset-0 bg-gradient-to-r from-emerald-600 to-emerald-400 opacity-90"
        ></div>
        <div class="relative px-8 py-6 flex justify-between items-center">
          <div>
            <h2 class="text-3xl font-bold text-white">추천 매물</h2>
            <p class="text-emerald-50 mt-1">당신의 스타일에 맞는 엄선된 매물입니다</p>
          </div>
          <button
            @click="close"
            class="w-10 h-10 rounded-full bg-white bg-opacity-20 flex items-center justify-center text-white hover:bg-opacity-30 transition-all duration-200"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-6 w-6"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </button>
        </div>
      </div>

      <!-- 탭 네비게이션 -->
      <div class="px-8 py-4 border-b bg-white sticky top-0 z-10">
        <div class="flex justify-between items-center">
          <!-- 탭 버튼 -->
          <nav class="flex space-x-1 bg-gray-100 p-1 rounded-lg">
            <button
              v-for="tab in tabs"
              :key="tab"
              @click="activeTab = tab"
              class="relative px-4 py-2 rounded-md font-medium transition-all duration-200"
              :class="
                activeTab === tab
                  ? 'bg-white text-emerald-600 shadow-sm'
                  : 'text-gray-600 hover:text-gray-800'
              "
            >
              {{ tabNames[tab] }}
            </button>
          </nav>

          <!-- 뷰 토글 -->
          <div class="inline-flex bg-gray-100 rounded-full p-1 shadow-inner">
            <!-- 그리드 뷰 버튼 -->
            <button
              @click="view = 'grid'"
              :class="
                view === 'grid'
                  ? 'bg-white text-emerald-600 shadow'
                  : 'text-gray-500 hover:bg-white hover:text-emerald-600'
              "
              class="px-3 py-2 rounded-full transition-all duration-200"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M4 4h6v6H4V4zM14 4h6v6h-6V4zM4 14h6v6H4v-6zM14 14h6v6h-6v-6z"
                />
              </svg>
            </button>

            <!-- 리스트 뷰 버튼 -->
            <button
              @click="view = 'list'"
              :class="
                view === 'list'
                  ? 'bg-white text-emerald-600 shadow'
                  : 'text-gray-500 hover:bg-white hover:text-emerald-600'
              "
              class="px-3 py-2 rounded-full transition-all duration-200"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M4 6h16M4 12h16M4 18h16"
                />
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- 매물 리스트 -->
      <div class="flex-1 overflow-auto p-8 bg-gray-50">
        <!-- 로딩 -->
        <div v-if="loading" class="flex flex-col items-center justify-center h-64">
          <svg
            class="animate-spin h-12 w-12 text-emerald-600 mb-4"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
          >
            <circle
              class="opacity-25"
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              stroke-width="4"
            />
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z" />
          </svg>
          <p class="text-gray-500">추천 매물을 불러오는 중입니다...</p>
        </div>

        <!-- 그리드/리스트 -->
        <div
          v-else
          :class="
            view === 'grid' ? 'grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6' : 'space-y-4'
          "
        >
          <div
            v-for="item in currentList"
            :key="item.aptSeq || item.id"
            class="group bg-white rounded-xl overflow-hidden shadow-md hover:shadow-xl transition-all duration-300"
          >
            <div :class="view === 'grid' ? 'flex flex-col h-full' : 'flex'">
              <div
                :class="
                  view === 'grid'
                    ? 'h-48 overflow-hidden relative'
                    : 'w-1/3 h-25 max-h-[200px] overflow-hidden relative'
                "
              >
                <img
                  :src="item.imgPath || '/placeholder.svg'"
                  alt="아파트 이미지"
                  class="block w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
                />
              </div>

              <div :class="view === 'grid' ? 'p-5 flex-1 flex flex-col' : 'p-5 w-2/3'">
                <div class="flex justify-between items-start mb-2">
                  <h3
                    class="text-lg font-semibold text-gray-800 group-hover:text-emerald-600 transition-colors duration-200"
                  >
                    {{ item.listingName || '정보 없음' }}
                  </h3>
                  <span class="bg-emerald-100 text-emerald-800 text-xs px-2 py-1 rounded-full">
                    {{ item.propertyType || '정보 없음' }}
                  </span>
                </div>
                <p class="text-emerald-600 font-bold mb-2">{{ formatNumber(item.price) }}</p>
                <p class="text-sm text-gray-500 mb-3">
                  {{ item.spec || '스펙 정보 없음' }}
                </p>
                <p class="text-sm text-gray-600 line-clamp-3 mb-4">
                  {{ item.description || '설명 없음' }}
                </p>

                <button
                  @click="goSidebar(item.aptSeq)"
                  class="text-emerald-600 text-sm font-medium hover:text-emerald-700 transition-colors flex items-center"
                >
                  지도로 이동
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-4 w-4 ml-1"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M9 5l7 7-7 7"
                    />
                  </svg>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 데이터 없을 때 -->
        <div
          v-if="!loading && currentList.length === 0"
          class="flex flex-col items-center justify-center py-16"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-16 w-16 text-gray-300 mb-4"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M19 14l-7 7m0 0l-7-7m7 7V3"
            />
          </svg>
          <h3 class="text-lg font-medium text-gray-700 mb-1">추천 매물이 없습니다</h3>
          <p class="text-gray-500 text-center max-w-md">
            현재 선택하신 카테고리에 추천 매물이 없습니다. 다른 카테고리를 선택해보세요.
          </p>
        </div>
      </div>

      <!-- 푸터 -->
      <div class="px-8 py-4 bg-white border-t flex justify-between items-center">
        <p class="text-sm text-gray-500">
          <span class="font-medium text-emerald-600">{{ currentList.length }}</span
          >개의 매물이 표시됨
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

// router
const router = useRouter()

//refs
const view = ref('grid') // 현재 뷰 모드 (grid/list)
const activeTab = ref('recent') // 현재 활성화된 탭
const recent = ref([])
const nearstation = ref([]) //<-- Todo : 변수명 변경 nearStation
const newlyweds = ref([])
const rec = ref([])
const loading = ref(true)

// constant
const tabs = ['recent', 'nearstation', 'newlyweds']
const tabNames = {
  recent: '최근 거래 매물',
  nearstation: '역세권 매물',
  newlyweds: '신혼 추천 매물',
}

// computed
const currentList = computed(() => {
  if (activeTab.value === 'recent') return recent.value
  if (activeTab.value === 'nearstation') return nearstation.value
  if (activeTab.value === 'newlyweds') return newlyweds.value
  return rec.value
})

// onMounted
onMounted(async () => {
  loading.value = true
  try {
    const [r, m, c] = await Promise.all([
      axios.get('http://localhost:8080/api/v1/house/recommend/recent?limit=6'),
      axios.get('http://localhost:8080/api/v1/house/recommend/nearstation?limit=6'),
      axios.get('http://localhost:8080/api/v1/house/recommend/newlyweds?limit=6'),
    ])
    recent.value = r.data
    nearstation.value = m.data
    newlyweds.value = c.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
})

// 닫기
function close() {
  router.back()
}

// 숫자 천 단위 콤마
function formatNumber(val) {
  return val != null ? val.toLocaleString() + '원' : '정보 없음'
}

function goSidebar(aptSeq) {
  // 모달 닫기
  close()
  // 메인 페이지로 이동하면서 detail 쿼리 전달
  router.push({ path: '/', query: { detail: aptSeq } })
}
</script>

<style scoped>
.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.scrollbar-hide::-webkit-scrollbar {
  display: none;
}

.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.backdrop-blur-sm {
  backdrop-filter: blur(4px);
}
</style>

<style scoped>
.img-full-cover > img {
  display: block;
  font-size: 0; /* inline 여백 제거 */
}
</style>
