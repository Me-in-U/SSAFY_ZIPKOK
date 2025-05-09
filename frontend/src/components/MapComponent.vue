<template>
    <div class="relative w-full h-full bg-white rounded-lg shadow overflow-hidden"
        style="position: relative; width: 100%;">
        <!-- 지도 플레이스홀더 - 실제 구현에서는 Google Maps, Kakao Maps 등의 API를 사용하세요 -->
        <div class="absolute inset-0 bg-gray-100">
            <div id="map-container" class="w-full h-full relative">
                <!-- 지도 배경 (실제 구현 시 이 부분은 지도 API로 대체) -->
                <div class="absolute inset-0 bg-gray-200 flex items-center justify-center">
                    <div v-if="properties.length === 0" class="text-gray-500 text-center">
                        <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none"
                            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                            class="mx-auto mb-2">
                            <path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z" />
                            <circle cx="12" cy="10" r="3" />
                        </svg>
                        <p>검색 결과가 없습니다</p>
                        <p class="text-sm mt-2">다른 검색 조건을 시도해보세요</p>
                    </div>
                </div>

                <!-- 지도 마커 (샘플) -->
                <div v-for="property in properties" :key="property.id"
                    class="absolute cursor-pointer transform -translate-x-1/2 -translate-y-1/2 hover:z-10 transition-all duration-200 hover:scale-110"
                    :style="{
                        top: `${30 + (property.id * 10) % 50}%`,
                        left: `${20 + (property.id * 15) % 60}%`,
                    }" @click="selectProperty(property)">
                    <div class="bg-emerald-600 text-white p-1 rounded-full shadow-lg">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
                            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z" />
                            <circle cx="12" cy="10" r="3" />
                        </svg>
                    </div>
                    <div
                        class="absolute bottom-full mb-1 left-1/2 transform -translate-x-1/2 bg-white px-2 py-1 rounded shadow-md text-xs whitespace-nowrap">
                        {{ property.title }}
                        <div class="text-emerald-600 font-medium">{{ property.price }}</div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 지도 컨트롤 -->
        <div class="absolute top-4 right-4 bg-white p-2 rounded-md shadow-md">
            <div class="flex flex-col gap-2">
                <button class="w-8 h-8 flex items-center justify-center border rounded hover:bg-gray-50">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none"
                        stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M5 12h14" />
                        <path d="M12 5v14" />
                    </svg>
                </button>
                <button class="w-8 h-8 flex items-center justify-center border rounded hover:bg-gray-50">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none"
                        stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M5 12h14" />
                    </svg>
                </button>
                <button class="w-8 h-8 flex items-center justify-center border rounded hover:bg-gray-50">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none"
                        stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="m7 10 5 5 5-5" />
                    </svg>
                </button>
            </div>
        </div>

        <!-- 지역 선택 -->
        <div class="absolute bottom-4 left-4 bg-white p-2 rounded-md shadow-md">
            <select class="text-sm border-0 focus:outline-none focus:ring-0" v-model="selectedLocation">
                <option value="">지역 선택</option>
                <option value="seoul">서울</option>
                <option value="gyeonggi">경기</option>
                <option value="incheon">인천</option>
                <option value="busan">부산</option>
                <option value="daegu">대구</option>
                <option value="daejeon">대전</option>
            </select>
        </div>

        <!-- 검색 결과 요약 -->
        <div class="absolute top-4 left-4 bg-white px-3 py-2 rounded-md shadow-md">
            <p class="text-sm font-medium">검색 결과: <span class="text-emerald-600">{{ properties.length }}개</span> 매물</p>
        </div>
    </div>
</template>

<script setup>
import { ref, defineProps, defineEmits } from 'vue';

// eslint-disable-next-line no-unused-vars
const props = defineProps({
    properties: {
        type: Array,
        required: true
    }
});

const emit = defineEmits(['select-property']);

const selectedLocation = ref('');

function selectProperty(property) {
    emit('select-property', property);
}
</script>

<style scoped>
.relative {
    position: relative;
}

.w-full {
    width: 100%;
}

.absolute {
    position: absolute;
}

.inset-0 {
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
}

.bg-gray-100 {
    background-color: #f7fafc;
}

.bg-gray-200 {
    background-color: #edf2f7;
}

.flex {
    display: flex;
}

.items-center {
    align-items: center;
}

.justify-center {
    justify-content: center;
}

.text-gray-500 {
    color: #a0aec0;
}

.text-center {
    text-align: center;
}

.mx-auto {
    margin-left: auto;
    margin-right: auto;
}

.mb-2 {
    margin-bottom: 0.5rem;
}

.cursor-pointer {
    cursor: pointer;
}

.transform {
    transform: translate(-50%, -50%);
}

.hover\:z-10:hover {
    z-index: 10;
}

.transition-all {
    transition: all 0.2s;
}

.duration-200 {
    transition-duration: 200ms;
}

.hover\:scale-110:hover {
    transform: scale(1.1);
}

.bg-emerald-600 {
    background-color: #059669;
}

.text-white {
    color: #ffffff;
}

.p-1 {
    padding: 0.25rem;
}

.rounded-full {
    border-radius: 9999px;
}

.shadow-lg {
    box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
}

.bottom-full {
    bottom: 100%;
}

.mb-1 {
    margin-bottom: 0.25rem;
}

.left-1\/2 {
    left: 50%;
}

.bg-white {
    background-color: #ffffff;
}

.px-2 {
    padding-left: 0.5rem;
    padding-right: 0.5rem;
}

.py-1 {
    padding-top: 0.25rem;
    padding-bottom: 0.25rem;
}

.rounded {
    border-radius: 0.25rem;
}

.shadow-md {
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.text-xs {
    font-size: 0.75rem;
}

.whitespace-nowrap {
    white-space: nowrap;
}

.text-emerald-600 {
    color: #059669;
}

.font-medium {
    font-weight: 500;
}

.top-4 {
    top: 1rem;
}

.right-4 {
    right: 1rem;
}

.p-2 {
    padding: 0.5rem;
}

.rounded-md {
    border-radius: 0.375rem;
}

.flex-col {
    flex-direction: column;
}

.gap-2 {
    gap: 0.5rem;
}

.w-8 {
    width: 2rem;
}

.h-8 {
    height: 2rem;
}

.items-center {
    align-items: center;
}

.justify-center {
    justify-content: center;
}

.border {
    border-width: 1px;
}

.hover\:bg-gray-50:hover {
    background-color: #f9fafb;
}

.bottom-4 {
    bottom: 1rem;
}

.left-4 {
    left: 1rem;
}

.text-sm {
    font-size: 0.875rem;
}

.border-0 {
    border-width: 0;
}

.focus\:outline-none:focus {
    outline: 2px solid transparent;
    outline-offset: 2px;
}

.focus\:ring-0:focus {
    box-shadow: 0 0 0 0 transparent;
}

.px-3 {
    padding-left: 0.75rem;
    padding-right: 0.75rem;
}

.py-2 {
    padding-top: 0.5rem;
    padding-bottom: 0.5rem;
}

.font-medium {
    font-weight: 500;
}
</style>