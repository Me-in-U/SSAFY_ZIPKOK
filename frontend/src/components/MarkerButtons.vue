<template>
  <button
    @click="$emit('toggle-base')"
    class="relative w-10 h-10 rounded-full flex items-center justify-center transition"
    :class="showBase ? 'bg-emerald-600 text-white' : 'bg-gray-200 text-gray-700'"
  >
    <!-- 기본 아이콘 -->
    <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
      <path
        d="M10.707 1.707a1 1 0 00-1.414 0L2 8.999V17a1 1 0 001 1h5a1 1 0 001-1v-4h2v4a1 1 0 001 1h5a1 1 0 001-1V8.999l-7.293-7.292z"
      />
    </svg>
    <!-- 비활성 시 X 표시 -->
    <svg
      v-if="!showBase"
      xmlns="http://www.w3.org/2000/svg"
      class="absolute w-3 h-3 top-0 right-0 text-red-500"
      fill="none"
      viewBox="0 0 24 24"
      stroke="currentColor"
    >
      <path
        stroke-linecap="round"
        stroke-linejoin="round"
        stroke-width="3"
        d="M6 6l12 12M6 18L18 6"
      />
    </svg>
  </button>

  <button
    @click="$emit('toggle-favorite')"
    class="relative w-10 h-10 rounded-full flex items-center justify-center transition"
    :class="showFavorite ? 'bg-emerald-600 text-white' : 'bg-gray-200 text-gray-700'"
  >
    <!-- 즐겨찾기 아이콘 -->
    <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
      <path
        d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.286 3.954a1 1 0 00.95.69h4.176c.969 0 1.371 1.24.588 1.81l-3.385 2.46a1 1 0 00-.364 1.118l1.287 3.953c.3.922-.755 1.688-1.54 1.118l-3.385-2.46a1 1 0 00-1.176 0l-3.385 2.46c-.784.57-1.838-.196-1.54-1.118l1.287-3.953a1 1 0 00-.364-1.118L2.049 9.38c-.783-.57-.38-1.81.588-1.81h4.176a1 1 0 00.95-.69l1.286-3.954z"
      />
    </svg>
    <svg
      v-if="!showFavorite"
      xmlns="http://www.w3.org/2000/svg"
      class="absolute w-3 h-3 top-0 right-0 text-red-500"
      fill="none"
      viewBox="0 0 24 24"
      stroke="currentColor"
    >
      <path
        stroke-linecap="round"
        stroke-linejoin="round"
        stroke-width="3"
        d="M6 6l12 12M6 18L18 6"
      />
    </svg>
  </button>

  <button
    @click="$emit('toggle-search')"
    class="relative w-10 h-10 rounded-full flex items-center justify-center transition"
    :class="showSearch ? 'bg-emerald-600 text-white' : 'bg-gray-200 text-gray-700'"
  >
    <!-- 검색 아이콘 -->
    <svg
      xmlns="http://www.w3.org/2000/svg"
      class="w-5 h-5"
      fill="none"
      viewBox="0 0 24 24"
      stroke="currentColor"
    >
      <circle cx="11" cy="11" r="8" stroke-width="2" />
      <line x1="21" y1="21" x2="16.65" y2="16.65" stroke-width="2" stroke-linecap="round" />
    </svg>
    <svg
      v-if="!showSearch"
      xmlns="http://www.w3.org/2000/svg"
      class="absolute w-3 h-3 top-0 right-0 text-red-500"
      fill="none"
      viewBox="0 0 24 24"
      stroke="currentColor"
    >
      <path
        stroke-linecap="round"
        stroke-linejoin="round"
        stroke-width="3"
        d="M6 6l12 12M6 18L18 6"
      />
    </svg>
  </button>

  <!-- 지도 모드 토글 버튼 -->
  <button
    @click="$emit('toggle-maptype', mapType === 'roadmap' ? 'skyview' : 'roadmap')"
    :class="mapType === 'roadmap' ? 'bg-emerald-600 text-white' : 'bg-gray-200 text-gray-700'"
    class="w-10 h-10 rounded-full flex items-center justify-center transition transform active:scale-95 hover:ring-2 focus:outline-none"
  >
    <!-- 지형도 아이콘 -->
    <svg
      v-if="mapType === 'roadmap'"
      xmlns="http://www.w3.org/2000/svg"
      class="w-5 h-5"
      fill="none"
      viewBox="0 0 24 24"
      stroke="currentColor"
      stroke-width="2"
    >
      <path
        stroke-linecap="round"
        stroke-linejoin="round"
        d="M3 6a1 1 0 011-1h3l2 6-2 6H4a1 1 0 01-1-1V6zM8 5h8l2 14H10L8 5zM16 5h3a1 1 0 011 1v12a1 1 0 01-1 1h-3V5z"
      />
    </svg>

    <!-- 위성지도(지구) 아이콘 -->
    <svg
      v-else
      xmlns="http://www.w3.org/2000/svg"
      class="w-5 h-5"
      fill="none"
      viewBox="0 0 24 24"
      stroke="currentColor"
      stroke-width="2"
    >
      <path
        stroke-linecap="round"
        stroke-linejoin="round"
        d="M12 2a10 10 0 100 20 10 10 0 000-20zm0 2c3.866 0 7 3.134 7 7s-3.134 7-7 7V4zM5 12c0-3.866 3.134-7 7-7v14c-3.866 0-7-3.134-7-7z"
      />
    </svg>
  </button>

  <!-- 확대 버튼 -->
  <button
    @click="$emit('zoom', 'in')"
    class="w-10 h-10 rounded-full bg-gray-200 text-gray-700 flex items-center justify-center transition transform active:scale-95 hover:ring-2 hover:ring-gray-400 focus:outline-none"
  >
    <span class="text-xl font-bold">＋</span>
  </button>

  <!-- 축소 버튼 -->
  <button
    @click="$emit('zoom', 'out')"
    class="w-10 h-10 rounded-full bg-gray-200 text-gray-700 flex items-center justify-center transition transform active:scale-95 hover:ring-2 hover:ring-gray-400 focus:outline-none"
  >
    <span class="text-xl font-bold">－</span>
  </button>
</template>

<script setup>
defineEmits(['toggle-base', 'toggle-favorite', 'toggle-search', 'toggle-maptype', 'zoom'])
defineProps({
  showBase: Boolean,
  showFavorite: Boolean,
  showSearch: Boolean,
  mapType: String,
})
</script>
