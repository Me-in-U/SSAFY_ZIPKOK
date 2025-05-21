<template>
  <div ref="wrapper" class="wrapper h-8 bg-emerald-100 overflow-x-auto overflow-y-hidden relative">
    <ul ref="ticker" class="inline-block whitespace-nowrap">
      <li v-for="(news, i) in newsList" :key="i" class="inline-block px-4 text-sm text-gray-700">
        <a
          :href="news.originallink"
          target="_blank"
          rel="noopener noreferrer"
          class="hover:underline"
        >
          {{ decodeHtml(news.title) }}
        </a>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useNewsStore } from '@/stores/news'
import { decodeHtml } from '@/utils/html'
// 스토어
const newsStore = useNewsStore()

// 상태
const newsList = ref([])
const wrapper = ref(null)
const limit = 5
const speed = 1.3 // px per frame
let rafId = null
let loadingNext = false

// 초기 로드
async function loadInitial() {
  await newsStore.init({ limit })
  newsList.value = [...newsStore.list]
  await nextTick()
}

// 애니메이션 루프
function tick() {
  const w = wrapper.value
  const t = w.querySelector('ul')

  w.scrollLeft += speed
  if (w.scrollLeft + w.clientWidth >= t.scrollWidth - 100 && !loadingNext) {
    loadingNext = true
    newsStore.fetchNext({ limit }).then(async () => {
      newsList.value.push(...newsStore.list)
      await nextTick()
      loadingNext = false
    })
  }
  rafId = requestAnimationFrame(tick)
}

onMounted(async () => {
  await loadInitial()
  await nextTick()
  wrapper.value.scrollLeft = 0
  tick()
})

onBeforeUnmount(() => {
  cancelAnimationFrame(rafId)
})
</script>

<style>
.wrapper::-webkit-scrollbar {
  display: none;
}
.wrapper {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
