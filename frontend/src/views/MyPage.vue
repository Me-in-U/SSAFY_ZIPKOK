<template>
    <main class="flex h-full">
        <!-- 좌측: 즐겨찾기 목록 -->
        <section class="w-1/3 bg-white p-4 overflow-auto border-r">
            <h1 class="text-2xl font-bold mb-4">내 즐겨찾기</h1>
            <div v-if="!favorites.length" class="text-gray-500">
                아직 즐겨찾기한 매물이 없습니다.
            </div>
            <div v-else class="grid grid-cols-1 gap-4">
                <div v-for="house in favorites" :key="house.aptSeq"
                    class="flex items-center space-x-4 p-3 border rounded hover:shadow cursor-pointer">
                    <img v-if="house.imgPath" :src="house.imgPath" alt="대표 이미지"
                        class="w-16 h-16 object-cover rounded" />
                    <div class="flex-1" @click="select(house)">
                        <h2 class="font-semibold">{{ house.aptNm }}</h2>
                        <p class="text-sm text-gray-600">
                            {{ house.roadAddress || house.jibunAddress }}
                        </p>
                    </div>
                    <button @click.stop="remove(house.aptSeq)" class="text-red-500 hover:text-red-700" title="즐겨찾기 해제">
                        ✕
                    </button>
                </div>
            </div>
        </section>

        <!-- 우측: 상세 사이드바 -->
        <PropertyDetailsSidebar v-if="selectedAptSeq" :aptSeq="selectedAptSeq" :is-favorite="true"
            @close="selectedAptSeq = null" @toggle-favorite="onToggleFavorite" class="w-2/3" />
    </main>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useUserStore } from '@/stores/user'
import PropertyDetailsSidebar from '@/components/PropertyDetailsSidebar.vue'

const userStore = useUserStore()
const favorites = ref([])
const selectedAptSeq = ref(null)

// 1) 마운트 시 즐겨찾기 불러오기
async function loadFavorites() {
    try {
        const mno = userStore.profile.mno
        const res = await axios.get(`/api/v1/members/${mno}/favorites`)
        favorites.value = Array.isArray(res.data.result) ? res.data.result : []
    } catch (e) {
        console.error('즐겨찾기 불러오기 실패', e)
    }
}

// 2) 매물 클릭 → 상세 보기
function select(house) {
    selectedAptSeq.value = house.aptSeq
}

// 3) 즐겨찾기 해제
async function remove(aptSeq) {
    try {
        const mno = userStore.profile.mno
        await axios.delete(`/api/v1/members/${mno}/favorites/${aptSeq}`)
        favorites.value = favorites.value.filter(h => h.aptSeq !== aptSeq)
        if (selectedAptSeq.value === aptSeq) selectedAptSeq.value = null
    } catch (e) {
        console.error('즐겨찾기 삭제 실패', e)
    }
}

// 4) 사이드바 북마크 토글 처리
function onToggleFavorite({ aptSeq, isFavorite }) {
    if (!isFavorite) {
        favorites.value = favorites.value.filter(h => h.aptSeq !== aptSeq)
        if (selectedAptSeq.value === aptSeq) selectedAptSeq.value = null
    }
}

onMounted(loadFavorites)
</script>

<style scoped>
main {
    height: calc(100vh - 4rem);
    /* 헤더 높이(약 4rem)만큼 빼기 */
}
</style>
  
