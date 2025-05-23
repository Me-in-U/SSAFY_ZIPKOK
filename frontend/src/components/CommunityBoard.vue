<template>
    <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-50">
        <div class="bg-white rounded-xl shadow-2xl w-full max-w-5xl max-h-[90vh] overflow-auto relative flex flex-col">
            <!-- 헤더 -->
            <div class="flex items-center justify-between px-6 py-4 border-b sticky top-0 bg-white z-10">
                <h2 class="text-2xl font-bold text-gray-800">부동산 커뮤니티</h2>
                <button @click="close" class="text-gray-400 hover:text-gray-700 text-xl">✕</button>
            </div>

            <!-- 카테고리 탭 -->
            <div class="px-6 py-2 border-b bg-gray-50 sticky top-[65px] z-10">
                <div class="flex space-x-2 overflow-x-auto pb-2 scrollbar-hide">
                    <button v-for="cat in categories" :key="cat.id" @click="activeCategory = cat.id"
                        class="px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-colors" :class="activeCategory === cat.id
                                ? 'bg-emerald-600 text-white'
                                : 'bg-white text-gray-700 hover:bg-gray-100'
                            ">
                        {{ cat.name }}
                    </button>
                </div>
            </div>

            <!-- 검색 및 글쓰기 버튼 -->
            <div class="px-6 py-3 bg-white border-b flex flex-wrap gap-3 items-center sticky top-[117px] z-10">
                <div class="relative flex-1 min-w-[200px]">
                    <input v-model="searchQuery" type="text" placeholder="검색어를 입력하세요"
                        class="w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                        @keyup.enter="searchPosts" />
                    <svg xmlns="http://www.w3.org/2000/svg"
                        class="h-5 w-5 absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" fill="none"
                        viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                    </svg>
                </div>
                <button @click="openPostForm"
                    class="bg-emerald-600 text-white px-4 py-2 rounded-lg hover:bg-emerald-700 transition flex items-center gap-2">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24"
                        stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                    </svg>
                    글쓰기
                </button>
            </div>

            <!-- 게시글 목록 -->
            <div class="flex-1 overflow-auto p-6">
                <div v-if="loading" class="flex justify-center items-center py-20">
                    <svg class="animate-spin h-10 w-10 text-emerald-600" xmlns="http://www.w3.org/2000/svg" fill="none"
                        viewBox="0 0 24 24">
                        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
                        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z" />
                    </svg>
                </div>

                <div v-else-if="posts.length === 0" class="text-center py-20 text-gray-500">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 mx-auto mb-4 text-gray-300" fill="none"
                        viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                    </svg>
                    <p class="text-lg">게시글이 없습니다</p>
                    <p class="mt-2">첫 번째 게시글을 작성해보세요!</p>
                </div>

                <div v-else class="space-y-4">
                    <div v-for="post in posts" :key="post.id"
                        class="border rounded-lg p-4 hover:shadow-md transition cursor-pointer"
                        @click="openPostDetail(post)">
                        <div class="flex justify-between items-start">
                            <div>
                                <span class="inline-block px-2 py-1 text-xs rounded-full mb-2"
                                    :class="getCategoryClass(post.categoryId)">
                                    {{ getCategoryName(post.categoryId) }}
                                </span>
                                <h3 class="text-lg font-medium text-gray-800">{{ post.title }}</h3>
                            </div>
                            <div class="flex items-center text-gray-500 text-sm">
                                <span class="flex items-center mr-3">
                                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none"
                                        viewBox="0 0 24 24" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                            d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                            d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                                    </svg>
                                    {{ post.views }}
                                </span>
                                <span class="flex items-center">
                                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none"
                                        viewBox="0 0 24 24" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                            d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
                                    </svg>
                                    {{ post.comments.length }}
                                </span>
                            </div>
                        </div>
                        <div class="mt-2 text-sm text-gray-600 line-clamp-2">{{ post.content }}</div>
                        <div class="mt-3 flex justify-between items-center text-xs text-gray-500">
                            <div class="flex items-center">
                                <div class="w-6 h-6 rounded-full bg-gray-300 mr-2 overflow-hidden">
                                    <img v-if="post.authorAvatar" :src="post.authorAvatar" alt="프로필"
                                        class="w-full h-full object-cover" />
                                    <div v-else
                                        class="w-full h-full flex items-center justify-center bg-emerald-100 text-emerald-800">
                                        {{ post.authorName.charAt(0) }}
                                    </div>
                                </div>
                                {{ post.authorName }}
                            </div>
                            <div>{{ formatDate(post.createdAt) }}</div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 페이지네이션 -->
            <div class="px-6 py-4 border-t flex justify-center">
                <div class="flex space-x-1">
                    <button @click="currentPage > 1 && (currentPage--)" :disabled="currentPage === 1"
                        class="px-3 py-1 rounded border" :class="currentPage === 1
                                ? 'text-gray-400 border-gray-200 cursor-not-allowed'
                                : 'text-gray-700 border-gray-300 hover:bg-gray-50'
                            ">
                        이전
                    </button>
                    <button v-for="page in totalPages" :key="page" @click="currentPage = page"
                        class="px-3 py-1 rounded border" :class="currentPage === page
                                ? 'bg-emerald-600 text-white border-emerald-600'
                                : 'text-gray-700 border-gray-300 hover:bg-gray-50'
                            ">
                        {{ page }}
                    </button>
                    <button @click="currentPage < totalPages && currentPage++" :disabled="currentPage === totalPages"
                        class="px-3 py-1 rounded border" :class="currentPage === totalPages
                                ? 'text-gray-400 border-gray-200 cursor-not-allowed'
                                : 'text-gray-700 border-gray-300 hover:bg-gray-50'
                            ">
                        다음
                    </button>
                </div>
            </div>
        </div>

        <!-- 게시글 상세 모달 -->
        <div v-if="selectedPost"
            class="fixed inset-0 z-[60] flex items-center justify-center p-4 bg-black bg-opacity-50">
            <div class="bg-white rounded-xl shadow-2xl w-full max-w-3xl max-h-[90vh] overflow-auto relative">
                <div class="sticky top-0 bg-white z-10 border-b px-6 py-4 flex justify-between items-center">
                    <h3 class="text-xl font-bold">게시글</h3>
                    <button @click="selectedPost = null" class="text-gray-400 hover:text-gray-700">
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24"
                            stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                d="M6 18L18 6M6 6l12 12" />
                        </svg>
                    </button>
                </div>

                <div class="p-6">
                    <div class="mb-2">
                        <span class="inline-block px-2 py-1 text-xs rounded-full"
                            :class="getCategoryClass(selectedPost.categoryId)">
                            {{ getCategoryName(selectedPost.categoryId) }}
                        </span>
                    </div>
                    <h2 class="text-2xl font-bold mb-4">{{ selectedPost.title }}</h2>

                    <div class="flex items-center justify-between mb-6 text-sm text-gray-500">
                        <div class="flex items-center">
                            <div class="w-8 h-8 rounded-full bg-gray-300 mr-2 overflow-hidden">
                                <img v-if="selectedPost.authorAvatar" :src="selectedPost.authorAvatar" alt="프로필"
                                    class="w-full h-full object-cover" />
                                <div v-else
                                    class="w-full h-full flex items-center justify-center bg-emerald-100 text-emerald-800">
                                    {{ selectedPost.authorName.charAt(0) }}
                                </div>
                            </div>
                            <span>{{ selectedPost.authorName }}</span>
                        </div>
                        <div class="flex items-center gap-4">
                            <span>{{ formatDate(selectedPost.createdAt) }}</span>
                            <span class="flex items-center">
                                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none"
                                    viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                                </svg>
                                {{ selectedPost.views }}
                            </span>
                        </div>
                    </div>

                    <div class="border-t border-b py-6 mb-6 whitespace-pre-wrap">
                        {{ selectedPost.content }}
                    </div>

                    <!-- 댓글 섹션 -->
                    <div>
                        <h4 class="text-lg font-medium mb-4">댓글 {{ selectedPost.comments.length }}개</h4>

                        <!-- 댓글 입력 -->
                        <div class="mb-6">
                            <textarea v-model="commentText" rows="3" placeholder="댓글을 입력하세요"
                                class="w-full p-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"></textarea>
                            <div class="flex justify-end mt-2">
                                <button @click="addComment"
                                    class="bg-emerald-600 text-white px-4 py-2 rounded-lg hover:bg-emerald-700 transition">
                                    댓글 작성
                                </button>
                            </div>
                        </div>

                        <!-- 댓글 목록 -->
                        <div class="space-y-4">
                            <div v-for="comment in selectedPost.comments" :key="comment.id" class="border-b pb-4">
                                <div class="flex justify-between items-center mb-2">
                                    <div class="flex items-center">
                                        <div class="w-6 h-6 rounded-full bg-gray-300 mr-2 overflow-hidden">
                                            <img v-if="comment.authorAvatar" :src="comment.authorAvatar" alt="프로필"
                                                class="w-full h-full object-cover" />
                                            <div v-else
                                                class="w-full h-full flex items-center justify-center bg-emerald-100 text-emerald-800">
                                                {{ comment.authorName.charAt(0) }}
                                            </div>
                                        </div>
                                        <span class="font-medium">{{ comment.authorName }}</span>
                                    </div>
                                    <span class="text-xs text-gray-500">{{ formatDate(comment.createdAt) }}</span>
                                </div>
                                <p class="text-gray-700">{{ comment.content }}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 글쓰기 모달 -->
        <div v-if="showPostForm"
            class="fixed inset-0 z-[60] flex items-center justify-center p-4 bg-black bg-opacity-50">
            <div class="bg-white rounded-xl shadow-2xl w-full max-w-3xl max-h-[90vh] overflow-auto relative">
                <div class="sticky top-0 bg-white z-10 border-b px-6 py-4 flex justify-between items-center">
                    <h3 class="text-xl font-bold">새 게시글 작성</h3>
                    <button @click="showPostForm = false" class="text-gray-400 hover:text-gray-700">
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24"
                            stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                d="M6 18L18 6M6 6l12 12" />
                        </svg>
                    </button>
                </div>

                <div class="p-6">
                    <form @submit.prevent="submitPost" class="space-y-4">
                        <div>
                            <label for="category" class="block text-sm font-medium text-gray-700 mb-1">카테고리</label>
                            <select id="category" v-model="newPost.categoryId" required
                                class="w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500">
                                <option value="" disabled>카테고리 선택</option>
                                <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                                    {{ cat.name }}
                                </option>
                            </select>
                        </div>

                        <div>
                            <label for="title" class="block text-sm font-medium text-gray-700 mb-1">제목</label>
                            <input id="title" v-model="newPost.title" type="text" required placeholder="제목을 입력하세요"
                                class="w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500" />
                        </div>

                        <div>
                            <label for="content" class="block text-sm font-medium text-gray-700 mb-1">내용</label>
                            <textarea id="content" v-model="newPost.content" rows="10" required placeholder="내용을 입력하세요"
                                class="w-full p-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"></textarea>
                        </div>

                        <div class="flex justify-end pt-4">
                            <button type="button" @click="showPostForm = false"
                                class="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 mr-2 hover:bg-gray-50">
                                취소
                            </button>
                            <button type="submit"
                                class="px-4 py-2 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700">
                                등록하기
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

// 라우터 및 스토어
const router = useRouter()
const userStore = useUserStore()

// 상태 관리
const loading = ref(true)
const posts = ref([])
const categories = ref([
    { id: 'all', name: '전체' },
    { id: 'invest', name: '투자 정보' },
    { id: 'review', name: '매물 리뷰' },
    { id: 'qa', name: '질문/답변' },
    { id: 'region', name: '지역 정보' },
    { id: 'free', name: '자유 게시판' }
])
const activeCategory = ref('all')
const currentPage = ref(1)
const totalPages = ref(5)
const searchQuery = ref('')
const selectedPost = ref(null)
const showPostForm = ref(false)
const commentText = ref('')

// 새 게시글 폼
const newPost = ref({
    categoryId: '',
    title: '',
    content: ''
})

// 컴포넌트 마운트 시 게시글 로드
onMounted(() => {
    fetchPosts()
})

// 카테고리 변경 감지
watch(activeCategory, () => {
    fetchPosts()
})

// 페이지 변경 감지
watch(currentPage, () => {
    fetchPosts()
})

// 게시글 불러오기 (실제 구현 시 API 호출로 대체)
function fetchPosts() {
    loading.value = true

    // 실제 구현 시 API 호출
    setTimeout(() => {
        // 더미 데이터
        const dummyPosts = [
            {
                id: 1,
                categoryId: 'invest',
                title: '2025년 부동산 시장 전망과 투자 전략',
                content: '최근 부동산 시장은 금리 인상과 정부 정책 변화로 인해 큰 변동성을 보이고 있습니다. 이 글에서는 2025년 부동산 시장 전망과 효과적인 투자 전략에 대해 알아보겠습니다.\n\n1. 시장 전망\n- 금리 안정화에 따른 매수세 회복 예상\n- 수도권 외곽 신도시 개발로 인한 가격 상승 가능성\n- 1인 가구 증가로 소형 주택 수요 지속 증가\n\n2. 투자 전략\n- 역세권 중소형 아파트 위주 투자 검토\n- 장기 보유 관점에서 입지 우선 고려\n- 월세 수익률 3% 이상 물건 발굴\n\n여러분의 투자 전략은 어떤가요? 댓글로 의견 나눠주세요.',
                authorName: '부동산전문가',
                authorAvatar: null,
                createdAt: '2025-05-20T09:30:00',
                views: 342,
                comments: [
                    {
                        id: 1,
                        authorName: '투자초보',
                        authorAvatar: null,
                        content: '좋은 정보 감사합니다. 역세권 중소형 아파트가 좋다고 하셨는데, 구체적으로 어느 지역이 유망할까요?',
                        createdAt: '2025-05-20T10:15:00'
                    },
                    {
                        id: 2,
                        authorName: '부동산전문가',
                        authorAvatar: null,
                        content: '신도시 개발 계획이 있는 지역 중에서 교통이 편리한 곳을 추천합니다. 특히 GTX 노선 인근은 주목할 만합니다.',
                        createdAt: '2025-05-20T11:05:00'
                    }
                ]
            },
            {
                id: 2,
                categoryId: 'review',
                title: '광주 서구 OO아파트 실거주 후기',
                content: '안녕하세요, 광주 서구 OO아파트에 1년간 거주한 후기를 공유합니다.\n\n【장점】\n- 교통: 지하철역까지 도보 7분, 버스정류장 바로 앞\n- 편의시설: 대형마트, 병원, 학교 모두 10분 이내 거리\n- 단지 내 조경이 잘 되어있고 산책로가 좋음\n\n【단점】\n- 일부 동은 도로 소음이 있음 (특히 101동, 102동)\n- 주차공간이 세대 수 대비 부족한 편\n- 여름철 일부 세대 햇빛 강함\n\n【투자 관점】\n최근 1년간 시세는 약 5% 상승했으며, 임대 수요도 꾸준합니다. 역세권이라 투자 가치는 있지만, 신축 단지가 인근에 들어설 예정이라 단기적으로는 주의가 필요합니다.',
                authorName: '실거주자',
                authorAvatar: null,
                createdAt: '2025-05-19T14:20:00',
                views: 187,
                comments: [
                    {
                        id: 3,
                        authorName: '광주시민',
                        authorAvatar: null,
                        content: '자세한 후기 감사합니다. 관리비는 어느 정도 나오나요?',
                        createdAt: '2025-05-19T15:30:00'
                    }
                ]
            },
            {
                id: 3,
                categoryId: 'qa',
                title: '전세 계약 시 주의사항 문의드립니다',
                content: '다음 달에 처음으로 전세 계약을 앞두고 있는 초보입니다. 계약 시 특히 주의해야 할 점이나 꼭 확인해야 할 사항이 있을까요? 선배님들의 조언 부탁드립니다.',
                authorName: '부동산초보',
                authorAvatar: null,
                createdAt: '2025-05-18T18:45:00',
                views: 256,
                comments: [
                    {
                        id: 4,
                        authorName: '경험자',
                        authorAvatar: null,
                        content: '등기부등본 확인은 필수입니다. 특히 선순위 권리관계와 근저당 설정 금액을 꼭 확인하세요.',
                        createdAt: '2025-05-18T19:10:00'
                    },
                    {
                        id: 5,
                        authorName: '공인중개사',
                        authorAvatar: null,
                        content: '전입신고와 확정일자 받는 것 잊지 마세요. 또한 계약금은 가능한 적게 (보통 10%) 주시고, 잔금 지급 당일 현장에서 집 상태를 꼼꼼히 확인하세요.',
                        createdAt: '2025-05-18T20:05:00'
                    }
                ]
            },
            {
                id: 4,
                categoryId: 'region',
                title: '광주 남구 발전 가능성 분석',
                content: '광주 남구 지역의 향후 발전 가능성에 대해 분석해보았습니다.\n\n1. 개발 계획\n- 도시재생 뉴딜사업 진행 중 (2023-2026)\n- 상업지구 확장 계획\n- 교통 인프라 개선 사업\n\n2. 주변 환경\n- 교육 여건: 명문 고등학교 및 대학 인접\n- 생활 편의성: 대형 쇼핑몰 입점 예정\n- 자연 환경: 공원 조성 사업 진행 중\n\n3. 투자 전망\n- 단기: 보합세 예상\n- 중장기: 상승 가능성 높음\n\n특히 OO동 일대는 재개발 호재가 있어 주목할 만합니다.',
                authorName: '지역전문가',
                authorAvatar: null,
                createdAt: '2025-05-17T11:20:00',
                views: 203,
                comments: []
            },
            {
                id: 5,
                categoryId: 'free',
                title: '부동산 투자로 성공한 나의 이야기',
                content: '안녕하세요, 10년간의 부동산 투자 경험을 공유합니다. 처음에는 작은 오피스텔 한 채로 시작해서 지금은 여러 물건을 보유하게 되었습니다.\n\n가장 중요한 것은 인내심과 시장 흐름을 읽는 안목이었습니다. 모든 물건이 성공적이진 않았지만, 실패에서도 많은 것을 배웠습니다.\n\n여러분들도 장기적인 관점에서 투자하시길 권합니다. 급하게 수익을 내려다 오히려 손해 보는 경우가 많더라고요.',
                authorName: '투자선배',
                authorAvatar: null,
                createdAt: '2025-05-16T16:30:00',
                views: 412,
                comments: [
                    {
                        id: 6,
                        authorName: '꿈나무',
                        authorAvatar: null,
                        content: '멋진 성공 스토리네요! 처음 시작할 때 자금은 어떻게 마련하셨나요?',
                        createdAt: '2025-05-16T17:15:00'
                    },
                    {
                        id: 7,
                        authorName: '투자선배',
                        authorAvatar: null,
                        content: '직장 생활하며 5년간 모은 돈으로 시작했습니다. 처음에는 소형 오피스텔로 시작해 임대 수익을 재투자하는 방식이었어요.',
                        createdAt: '2025-05-16T18:20:00'
                    }
                ]
            }
        ]

        // 카테고리 필터링
        if (activeCategory.value !== 'all') {
            posts.value = dummyPosts.filter(post => post.categoryId === activeCategory.value)
        } else {
            posts.value = dummyPosts
        }

        // 검색어 필터링
        if (searchQuery.value) {
            posts.value = posts.value.filter(post =>
                post.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
                post.content.toLowerCase().includes(searchQuery.value.toLowerCase())
            )
        }

        loading.value = false
    }, 500)
}

// 검색 실행
function searchPosts() {
    currentPage.value = 1
    fetchPosts()
}

// 게시글 상세 보기
function openPostDetail(post) {
    // 실제 구현 시 API 호출로 조회수 증가 처리
    post.views++
    selectedPost.value = { ...post }
}

// 글쓰기 폼 열기
function openPostForm() {
    if (!userStore.isLogged) {
        alert('로그인이 필요한 서비스입니다.')
        router.push('/login')
        return
    }

    showPostForm.value = true
    newPost.value = {
        categoryId: '',
        title: '',
        content: ''
    }
}

// 게시글 등록
function submitPost() {
    // 실제 구현 시 API 호출
    const newPostData = {
        id: Date.now(),
        ...newPost.value,
        authorName: userStore.profile?.name || '익명',
        authorAvatar: null,
        createdAt: new Date().toISOString(),
        views: 0,
        comments: []
    }

    posts.value.unshift(newPostData)
    showPostForm.value = false

    // 카테고리가 현재 선택된 카테고리와 다르면 해당 카테고리로 이동
    if (activeCategory.value !== 'all' && activeCategory.value !== newPost.value.categoryId) {
        activeCategory.value = newPost.value.categoryId
    }
}

// 댓글 추가
function addComment() {
    if (!commentText.value.trim()) return

    if (!userStore.isLogged) {
        alert('로그인이 필요한 서비스입니다.')
        router.push('/login')
        return
    }

    // 실제 구현 시 API 호출
    const newComment = {
        id: Date.now(),
        authorName: userStore.profile?.name || '익명',
        authorAvatar: null,
        content: commentText.value,
        createdAt: new Date().toISOString()
    }

    selectedPost.value.comments.push(newComment)

    // 원본 게시글에도 댓글 추가
    const originalPost = posts.value.find(p => p.id === selectedPost.value.id)
    if (originalPost) {
        originalPost.comments.push(newComment)
    }

    commentText.value = ''
}

// 카테고리 이름 가져오기
function getCategoryName(categoryId) {
    const category = categories.value.find(c => c.id === categoryId)
    return category ? category.name : '기타'
}

// 카테고리별 스타일 클래스
function getCategoryClass(categoryId) {
    switch (categoryId) {
        case 'invest':
            return 'bg-blue-100 text-blue-800'
        case 'review':
            return 'bg-purple-100 text-purple-800'
        case 'qa':
            return 'bg-yellow-100 text-yellow-800'
        case 'region':
            return 'bg-green-100 text-green-800'
        case 'free':
            return 'bg-gray-100 text-gray-800'
        default:
            return 'bg-gray-100 text-gray-800'
    }
}

// 날짜 포맷팅
function formatDate(dateString) {
    const date = new Date(dateString)
    const now = new Date()
    const diffMs = now - date
    const diffSec = Math.floor(diffMs / 1000)
    const diffMin = Math.floor(diffSec / 60)
    const diffHour = Math.floor(diffMin / 60)
    const diffDay = Math.floor(diffHour / 24)

    if (diffDay > 0) {
        return `${diffDay}일 전`
    } else if (diffHour > 0) {
        return `${diffHour}시간 전`
    } else if (diffMin > 0) {
        return `${diffMin}분 전`
    } else {
        return '방금 전'
    }
}

// 모달 닫기
function close() {
    router.back()
}
</script>

<style scoped>
.scrollbar-hide::-webkit-scrollbar {
    display: none;
}

.scrollbar-hide {
    -ms-overflow-style: none;
    scrollbar-width: none;
}

.line-clamp-2 {
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}
</style>
  
