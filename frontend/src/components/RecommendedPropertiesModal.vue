<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-50">
    <div
      class="bg-white rounded-xl shadow-2xl w-full max-w-5xl max-h-[90vh] overflow-auto relative"
    >
      <!-- 헤더 -->
      <div class="flex items-center justify-between px-6 py-4 border-b">
        <h2 class="text-2xl font-bold text-gray-800">추천 매물</h2>
        <button @click="close" class="text-gray-400 hover:text-gray-700 text-xl">✕</button>
      </div>

      <!-- 뷰 모드 & 탭 -->
      <div class="px-6 py-4 bg-emerald-600 rounded-b-xl">
        <div class="flex justify-between items-center mb-4">
          <!-- 뷰 토글 -->
          <div class="inline-flex bg-gray-100 rounded-full p-1 shadow-inner">
            <!-- 그리드 뷰 버튼 -->
            <button
              @click="view = 'grid'"
              :class="
                view === 'grid'
                  ? 'bg-white text-emerald-600'
                  : 'text-gray-500 hover:bg-white hover:text-emerald-600'
              "
              class="px-3 py-2 rounded-l-full transition"
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
                  d="M4 4h6v6H4V4zM14 4h6v6h-6V4zM4 14h6v6H4v-6zM14 14h6v6h-6v-6z"
                />
              </svg>
            </button>

            <!-- 리스트 뷰 버튼 -->
            <button
              @click="view = 'list'"
              :class="
                view === 'list'
                  ? 'bg-white text-emerald-600'
                  : 'text-gray-500 hover:bg-white hover:text-emerald-600'
              "
              class="px-3 py-2 rounded-r-full transition"
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
                  d="M4 6h16M4 12h16M4 18h16"
                />
              </svg>
            </button>
          </div>

          <!-- 탭 네비 -->
          <nav class="flex space-x-4">
            <button
              v-for="tab in tabs"
              :key="tab"
              @click="activeTab = tab"
              class="relative px-3 py-2 font-medium transition"
              :class="activeTab === tab ? 'text-white' : 'text-white/70 hover:text-white'"
            >
              {{ tabNames[tab] }}
              <span
                v-if="activeTab === tab"
                class="absolute inset-x-0 -bottom-1 h-0.5 bg-white transition-all"
              />
            </button>
          </nav>
        </div>
      </div>

      <!-- 매물 리스트 -->
      <div class="px-6 py-6 bg-gray-50">
        <!-- 로딩 -->
        <div v-if="loading" class="flex items-center justify-center h-64">
          <svg
            class="animate-spin h-10 w-10 text-emerald-600"
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
            class="bg-white rounded-lg overflow-hidden shadow hover:shadow-lg transition"
          >
            <div :class="view === 'grid' ? '' : 'flex'">
              <div :class="view === 'grid' ? 'h-48' : 'w-1/3 h-32'">
                <img
                  :src="item.imgPath || '/placeholder.svg'"
                  alt="아파트"
                  class="w-full h-full object-cover"
                />
              </div>
              <div :class="view === 'grid' ? 'p-4' : 'p-4 w-2/3'">
                <h3 class="text-lg font-semibold text-gray-800 mb-1">
                  {{ item.listingName || '정보 없음' }}
                </h3>
                <p class="text-emerald-600 font-bold mb-2">{{ formatNumber(item.price) }}원</p>
                <p class="text-sm text-gray-500 mb-2">
                  {{ item.spec || '스펙 정보 없음' }}
                </p>
                <p class="text-sm text-gray-600 line-clamp-3">
                  {{ item.description || '설명 없음' }}
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

//refs
const view = ref('grid') // 현재 뷰 모드 (grid/list)
const activeTab = ref('recent') // 현재 활성화된 탭
const recent = ref([])
const nearstation = ref([]) //<-- Todo : 변수명 변경 nearStation
const newlyweds = ref([])
const rec = ref([])
const loading = ref(true)

// router
const router = useRouter()

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
      axios.get('https://api.ssafy.blog/api/v1/house/recommend/recent?limit=6'),
      axios.get('https://api.ssafy.blog/api/v1/house/recommend/nearstation?limit=6'),
      axios.get('https://api.ssafy.blog/api/v1/house/recommend/newlyweds?limit=6'),
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
  return val != null ? val.toLocaleString() : ''
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
</style>
