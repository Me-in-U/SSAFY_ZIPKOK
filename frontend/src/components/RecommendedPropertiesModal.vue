<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-50">
    <div class="bg-white rounded-lg shadow-lg w-full max-w-4xl max-h-[90vh] overflow-auto relative">
      <!-- 헤더 -->
      <div class="p-4 border-b flex items-center justify-between">
        <h2 class="text-xl font-semibold">추천 매물</h2>
        <button @click="close" class="text-gray-600 hover:text-gray-900">✕</button>
      </div>

      <!-- 뷰 모드 & 탭 -->
      <div class="p-4 border-b space-y-4">
        <div class="flex items-center gap-2">
          <button @click="view = 'grid'" :class="buttonClass(view === 'grid')">그리드</button>
          <button @click="view = 'list'" :class="buttonClass(view === 'list')">리스트</button>
        </div>
        <div class="flex border-b">
          <button
            v-for="tab in tabs"
            :key="tab"
            @click="activeTab = tab"
            :class="tab === activeTab ? 'border-b-2 border-primary font-medium' : ''"
            class="px-4 py-2 flex-1"
          >
            {{ tabNames[tab] }}
          </button>
        </div>
      </div>

      <!-- 매물 리스트 -->
      <div class="p-4">
        <div
          :class="
            view === 'grid' ? 'grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4' : 'space-y-4'
          "
        >
          <div
            v-for="item in currentList"
            :key="item.aptSeq || item.id"
            class="border rounded-lg overflow-hidden"
          >
            <div :class="view === 'grid' ? '' : 'flex'">
              <div :class="view === 'grid' ? 'w-full h-48' : 'w-1/3 h-32'">
                <img
                  :src="item.imgPath || '/placeholder.svg'"
                  class="w-full h-full object-cover"
                  alt="아파트"
                />
              </div>
              <div :class="view === 'grid' ? 'p-4' : 'p-4 w-2/3'">
                <h3 class="font-semibold text-lg mb-1">
                  {{ item.listingName || '정보 없음' }}
                </h3>
                <div class="text-sm text-gray-700 mb-2">
                  <span class="font-medium">{{ formatNumber(item.price) }}원</span>
                </div>
                <div class="text-sm text-gray-500 mb-2">
                  {{ item.spec || '스펙 정보 없음' }}
                </div>
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

const router = useRouter()

// 뷰 모드 / 탭 상태
const view = ref('grid')
const activeTab = ref('recent')
const tabs = ['recent', 'nearstation', 'newlyweds']
const tabNames = {
  recent: '최근 거래 매물',
  nearstation: '역세권 매물',
  newlyweds: '신혼 추천 매물',
}

// 데이터 상태
const recent = ref([])
const nearstation = ref([])
const newlyweds = ref([])
const rec = ref([])

const currentList = computed(() => {
  if (activeTab.value === 'recent') return recent.value
  if (activeTab.value === 'nearstation') return nearstation.value
  if (activeTab.value === 'newlyweds') return newlyweds.value
  return rec.value
})

// 데이터 불러오기
onMounted(async () => {
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
  }
})

// 닫기
function close() {
  router.back()
}

// 버튼 클래스 헬퍼
function buttonClass(active) {
  return active ? 'bg-primary text-white px-3 py-1 rounded' : 'border px-3 py-1 rounded'
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
