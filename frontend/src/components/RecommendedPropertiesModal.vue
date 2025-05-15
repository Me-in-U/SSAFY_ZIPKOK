<template>
    <div v-if="show" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-50">
        <div class="bg-white rounded-lg shadow-lg w-full max-w-4xl max-h-[90vh] overflow-auto">
            <div class="p-4 border-b flex items-center justify-between">
                <h2 class="text-xl font-semibold">추천 매물</h2>
                <button @click="$emit('close')">✕</button>
            </div>
            <div class="p-4">
                <!-- 뷰 모드 버튼 -->
                <div class="flex items-center justify-between mb-4">
                    <div class="flex items-center gap-2">
                        <button @click="view = 'grid'"
                            :class="view === 'grid' ? 'bg-primary text-white' : 'border'">그리드</button>
                        <button @click="view = 'list'"
                            :class="view === 'list' ? 'bg-primary text-white' : 'border'">리스트</button>
                    </div>
                </div>
                <!-- 탭 -->
                <div class="border-b mb-4">
                    <div class="flex">
                        <button v-for="tab in tabs" :key="tab.id" @click="activeTab = tab.id"
                            :class="activeTab === tab.id ? 'border-b-2 border-primary font-medium' : ''"
                            class="px-4 py-2 flex-1">
                            {{ tab.name }}
                        </button>
                    </div>
                </div>
                <!-- 매물 카드 -->
                <div :class="view === 'grid' ? 'grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4' : 'space-y-4'">
                    <div v-for="property in currentProperties" :key="property.aptSeq"
                        class="border rounded-lg overflow-hidden">
                        <div :class="view === 'grid' ? '' : 'flex'">
                            <!-- 이미지 -->
                            <div :class="view === 'grid' ? 'w-full h-48' : 'w-1/3 h-32'">
                                <img :src="property.imgPath || '/placeholder.svg'" class="w-full h-full object-cover"
                                    alt="아파트 이미지" />
                            </div>
                            <!-- 주요 정보 -->
                            <div :class="view === 'grid' ? 'p-4' : 'p-4 w-2/3'">
                                <h3 class="font-semibold text-lg mb-1">{{ property.listingName || '정보 없음' }}</h3>
                                <div class="text-sm text-gray-700 mb-2">
                                    <span class="font-medium">{{ formatNumber(property.price) }}원</span>
                                </div>
                                <div class="text-sm text-gray-500 mb-2">{{ property.spec || '스펙 정보 없음' }}</div>
                                <p class="text-sm text-gray-600 line-clamp-3">{{ property.description || '설명 없음' }}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, defineProps, defineEmits, watch } from 'vue';

const props = defineProps({
    show: Boolean,
    recentProperties: { type: Array, default: () => [] },
    mostProperties: { type: Array, default: () => [] },
    recommendedProperties: { type: Array, default: () => [] },
});
defineEmits(['close']);

const view = ref('grid');
const activeTab = ref('recent');
const tabs = [
    { id: 'recent', name: '최근 거래 매물' },
    { id: 'most', name: '최다 거래 매물' },
    { id: 'recommended', name: '추천 매물' },
];

const currentProperties = computed(() => {
    if (activeTab.value === 'recent') return props.recentProperties;
    else if (activeTab.value === 'most') return props.mostProperties;
    else return props.recommendedProperties;
});

// ★ 이 부분만 바꿔주세요 ★
// currentProperties 가 바뀔 때마다, 깊이 있게(deep) 전체 구조를 찍어줍니다.
watch(
    currentProperties,
    (newList) => {
        console.log(`[Modal] activeTab=${activeTab.value}`, newList);
    },
    { immediate: true, deep: true }
);

// 숫자 천단위 콤마 함수
const formatNumber = (value) => {
    if (value == null) return '';
    return value.toLocaleString();
};
</script>

<style scoped>
.line-clamp-3 {
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
}
</style>
  
