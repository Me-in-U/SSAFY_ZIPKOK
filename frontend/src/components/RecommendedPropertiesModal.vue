<template>
    <div v-if="show" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-50">
        <div class="bg-white rounded-lg shadow-lg w-full max-w-4xl max-h-[90vh] overflow-auto">
            <div class="p-4 border-b flex items-center justify-between">
                <h2 class="text-xl font-semibold">추천 매물</h2>
                <button @click="$emit('close')">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none"
                        stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                        class="lucide lucide-x">
                        <path d="M18 6 6 18" />
                        <path d="m6 6 12 12" />
                    </svg>
                </button>
            </div>

            <div class="p-4">
                <div class="flex items-center justify-between mb-4">
                    <div class="flex items-center gap-2">
                        <button class="px-3 py-1 text-sm rounded-md"
                            :class="{ 'bg-primary text-white': view === 'grid', 'border': view !== 'grid' }"
                            @click="view = 'grid'">
                            그리드
                        </button>
                        <button class="px-3 py-1 text-sm rounded-md"
                            :class="{ 'bg-primary text-white': view === 'list', 'border': view !== 'list' }"
                            @click="view = 'list'">
                            리스트
                        </button>
                    </div>
                </div>

                <div class="border-b mb-4">
                    <div class="flex">
                        <button v-for="tab in tabs" :key="tab.id" class="px-4 py-2 flex-1"
                            :class="{ 'border-b-2 border-primary font-medium': activeTab === tab.id }"
                            @click="activeTab = tab.id">
                            {{ tab.name }}
                        </button>
                    </div>
                </div>

                <div v-if="activeTab === 'recommended'">
                    <div
                        :class="view === 'grid' ? 'grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4' : 'space-y-4'">
                        <div v-for="property in properties" :key="property.id"
                            class="border rounded-lg overflow-hidden">
                            <div :class="view === 'grid' ? '' : 'flex'">
                                <div :class="view === 'grid' ? 'w-full' : 'w-1/3'">
                                    <img :src="property.image || '/placeholder.svg'" :alt="property.title"
                                        class="w-full h-full object-cover" />
                                </div>
                                <div :class="view === 'grid' ? 'p-4' : 'p-4 w-2/3'">
                                    <div class="flex items-start justify-between">
                                        <div>
                                            <h3 class="font-semibold">{{ property.title }}</h3>
                                            <div class="flex items-center text-sm text-gray-500 mt-1">
                                                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12"
                                                    viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                                    stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                                                    class="lucide lucide-map-pin mr-1">
                                                    <path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z" />
                                                    <circle cx="12" cy="10" r="3" />
                                                </svg>
                                                {{ property.location }}
                                            </div>
                                        </div>
                                        <span class="px-2 py-1 bg-gray-100 text-xs rounded-md">{{ property.type
                                        }}</span>
                                    </div>

                                    <div class="mt-4 grid grid-cols-2 gap-2">
                                        <div>
                                            <div class="text-sm text-gray-500">가격</div>
                                            <div class="font-medium">{{ property.price }}</div>
                                        </div>
                                        <div>
                                            <div class="text-sm text-gray-500">면적</div>
                                            <div class="font-medium">{{ property.size }}</div>
                                        </div>
                                        <div>
                                            <div class="text-sm text-gray-500">예상 수익률</div>
                                            <div class="font-medium flex items-center">
                                                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12"
                                                    viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                                    stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                                                    class="lucide lucide-trending-up mr-1 text-green-500">
                                                    <polyline points="22 7 13.5 15.5 8.5 10.5 2 17" />
                                                    <polyline points="16 7 22 7 22 13" />
                                                </svg>
                                                {{ property.roi }}
                                            </div>
                                        </div>
                                        <div>
                                            <div class="text-sm text-gray-500">가격 변동</div>
                                            <div class="font-medium text-green-500">{{ property.priceChange }}</div>
                                        </div>
                                    </div>

                                    <div class="mt-4 flex justify-end">
                                        <button class="px-3 py-1 border rounded-md text-sm mr-2">상세 보기</button>
                                        <button class="px-3 py-1 bg-primary text-white rounded-md text-sm">투자
                                            분석</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div v-else-if="activeTab === 'highRoi'" class="flex items-center justify-center h-32 text-gray-500">
                    수익률 높은 매물 목록이 여기에 표시됩니다
                </div>

                <div v-else-if="activeTab === 'trending'" class="flex items-center justify-center h-32 text-gray-500">
                    상승세 매물 목록이 여기에 표시됩니다
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, defineProps, defineEmits } from 'vue';

// eslint-disable-next-line no-unused-vars
const props = defineProps({
    show: {
        type: Boolean,
        default: false
    },
    properties: {
        type: Array,
        required: true
    }
});

defineEmits(['close']);

const view = ref('grid');
const activeTab = ref('recommended');

const tabs = [
    { id: 'recommended', name: '추천 매물' },
    { id: 'highRoi', name: '수익률 높은 매물' },
    { id: 'trending', name: '상승세 매물' }
];
</script>

<style scoped>
/* No !important declarations found in this file */
</style>