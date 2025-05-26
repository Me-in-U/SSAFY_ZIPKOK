<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-50">
    <div
      class="bg-white rounded-xl shadow-2xl w-full max-w-5xl h-[80vh] overflow-hidden relative flex flex-col"
    >
      <!-- 헤더 -->
      <div
        class="flex items-center justify-between px-6 py-4 border-b sticky top-0 bg-white z-10"
        style="height: 64px"
      >
        <h2 class="text-2xl font-bold text-gray-800">부동산 커뮤니티</h2>
        <button @click="close" class="text-gray-400 hover:text-gray-700 text-xl">✕</button>
      </div>

      <!-- 카테고리 탭 -->
      <div
        class="px-6 py-2 border-b bg-gray-50 sticky z-10"
        :style="{ top: HEADER_HEIGHT + 'px', height: TAB_HEIGHT + 5 + 'px' }"
      >
        <div class="flex space-x-2 overflow-x-auto pb-2 scrollbar-hide">
          <button
            v-for="cat in categories"
            :key="cat.id"
            @click="((activeCategory = cat.id), (currentPage = 1))"
            class="px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-colors"
            :class="
              activeCategory === cat.id
                ? 'bg-emerald-600 text-white'
                : 'bg-white text-gray-700 hover:bg-gray-100'
            "
          >
            {{ cat.name }}
          </button>
        </div>
      </div>

      <!-- 검색 및 글쓰기 버튼 -->
      <div
        class="px-6 py-3 bg-white border-b flex flex-wrap gap-3 items-center sticky z-10"
        :style="{ top: HEADER_HEIGHT + TAB_HEIGHT + 'px' }"
      >
        <div class="relative flex-1 min-w-[200px]">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="검색어를 입력하세요"
            class="w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
            @keyup.enter="searchPosts"
          />
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5 absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
            />
          </svg>
        </div>
        <button
          @click="openPostForm"
          class="bg-emerald-600 text-white px-4 py-2 rounded-lg hover:bg-emerald-700 transition flex items-center gap-2"
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
              d="M12 4v16m8-8H4"
            />
          </svg>
          글쓰기
        </button>
      </div>

      <!-- 게시글 목록 -->
      <div class="flex-1 overflow-auto p-6">
        <div v-if="loading" class="flex justify-center items-center py-20">
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

        <div v-else-if="posts.length === 0" class="text-center py-20 text-gray-500">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-16 w-16 mx-auto mb-4 text-gray-300"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
            />
          </svg>
          <p class="text-lg">게시글이 없습니다</p>
          <p class="mt-2">첫 번째 게시글을 작성해보세요!</p>
        </div>

        <div v-else class="space-y-4">
          <div
            v-for="post in posts"
            :key="post.id"
            class="border rounded-lg p-4 hover:shadow-md transition cursor-pointer"
            @click="openPostDetail(post)"
          >
            <div class="flex justify-between items-start">
              <div>
                <span
                  class="inline-block px-2 py-1 text-xs rounded-full mb-2"
                  :class="getCategoryClass(post.categoryId)"
                >
                  {{ getCategoryName(post.categoryId) }}
                </span>
                <h3 class="text-lg font-medium text-gray-800">{{ post.title }}</h3>
              </div>
              <div class="flex items-center text-gray-500 text-sm">
                <span class="flex items-center mr-3">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-4 w-4 mr-1"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
                    />
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"
                    />
                  </svg>
                  {{ post.views }}
                </span>
                <span class="flex items-center">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-4 w-4 mr-1"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"
                    />
                  </svg>
                  {{ post.commentsCount }}
                </span>
              </div>
            </div>
            <div class="mt-2 text-sm text-gray-600 line-clamp-2">{{ post.content }}</div>
            <div class="mt-3 flex justify-between items-center text-xs text-gray-500">
              <div class="flex items-center">
                <div class="w-6 h-6 rounded-full bg-gray-300 mr-2 overflow-hidden">
                  <img
                    v-if="post.authorAvatar"
                    :src="post.authorAvatar"
                    alt="프로필"
                    class="w-full h-full object-cover"
                  />
                  <div
                    v-else
                    class="w-full h-full flex items-center justify-center bg-emerald-100 text-emerald-800"
                  >
                    {{ post.authorName?.charAt(0) || '익' }}
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
          <button
            @click="currentPage > 1 && currentPage--"
            :disabled="currentPage === 1"
            class="px-3 py-1 rounded border"
            :class="
              currentPage === 1
                ? 'text-gray-400 border-gray-200 cursor-not-allowed'
                : 'text-gray-700 border-gray-300 hover:bg-gray-50'
            "
          >
            이전
          </button>
          <button
            v-for="page in totalPages"
            :key="page"
            @click="currentPage = page"
            class="px-3 py-1 rounded border"
            :class="
              currentPage === page
                ? 'bg-emerald-600 text-white border-emerald-600'
                : 'text-gray-700 border-gray-300 hover:bg-gray-50'
            "
          >
            {{ page }}
          </button>
          <button
            @click="currentPage < totalPages && currentPage++"
            :disabled="currentPage === totalPages"
            class="px-3 py-1 rounded border"
            :class="
              currentPage === totalPages
                ? 'text-gray-400 border-gray-200 cursor-not-allowed'
                : 'text-gray-700 border-gray-300 hover:bg-gray-50'
            "
          >
            다음
          </button>
        </div>
      </div>
    </div>

    <!-- 게시글 상세 모달 -->
    <div
      v-if="selectedPost"
      class="fixed inset-0 z-[60] flex items-center justify-center p-4 bg-black bg-opacity-50"
    >
      <div
        class="bg-white rounded-xl shadow-2xl w-full max-w-3xl max-h-[90vh] overflow-auto relative"
      >
        <div
          class="sticky top-0 bg-white z-10 border-b px-6 py-4 flex justify-between items-center"
        >
          <h3 class="text-xl font-bold">게시글</h3>
          <button @click="selectedPost = null" class="text-gray-400 hover:text-gray-700">
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

        <div class="p-6">
          <div class="mb-2">
            <span
              class="inline-block px-2 py-1 text-xs rounded-full"
              :class="getCategoryClass(selectedPost.categoryId)"
            >
              {{ getCategoryName(selectedPost.categoryId) }}
            </span>
          </div>
          <h2 class="text-2xl font-bold mb-4">{{ selectedPost.title }}</h2>

          <div class="flex items-center justify-between mb-6 text-sm text-gray-500">
            <div class="flex items-center">
              <div class="w-8 h-8 rounded-full bg-gray-300 mr-2 overflow-hidden">
                <img
                  v-if="selectedPost.authorAvatar"
                  :src="selectedPost.authorAvatar"
                  alt="프로필"
                  class="w-full h-full object-cover"
                />
                <div
                  v-else
                  class="w-full h-full flex items-center justify-center bg-emerald-100 text-emerald-800"
                >
                  {{ selectedPost.authorName?.charAt(0) || '익' }}
                </div>
              </div>
              <span>{{ selectedPost.authorName }}</span>
            </div>
            <div class="flex items-center gap-4">
              <span>{{ formatDate(selectedPost.createdAt) }}</span>
              <span class="flex items-center">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-4 w-4 mr-1"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
                  />
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"
                  />
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
              <textarea
                v-model="commentText"
                rows="3"
                placeholder="댓글을 입력하세요"
                class="w-full p-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
              ></textarea>
              <div class="flex justify-end mt-2">
                <button
                  @click="addComment"
                  class="bg-emerald-600 text-white px-4 py-2 rounded-lg hover:bg-emerald-700 transition"
                >
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
                      <img
                        v-if="comment.authorAvatar"
                        :src="comment.authorAvatar"
                        alt="프로필"
                        class="w-full h-full object-cover"
                      />
                      <div
                        v-else
                        class="w-full h-full flex items-center justify-center bg-emerald-100 text-emerald-800"
                      >
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
    <div
      v-if="showPostForm"
      class="fixed inset-0 z-[60] flex items-center justify-center p-4 bg-black bg-opacity-50"
    >
      <div
        class="bg-white rounded-xl shadow-2xl w-full max-w-3xl max-h-[90vh] overflow-auto relative"
      >
        <div
          class="sticky top-0 bg-white z-10 border-b px-6 py-4 flex justify-between items-center"
        >
          <h3 class="text-xl font-bold">새 게시글 작성</h3>
          <button @click="showPostForm = false" class="text-gray-400 hover:text-gray-700">
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

        <div class="p-6">
          <form @submit.prevent="submitPost" class="space-y-4">
            <div>
              <label for="category" class="block text-sm font-medium text-gray-700 mb-1"
                >카테고리</label
              >
              <select
                id="category"
                v-model="newPost.categoryId"
                required
                class="w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
              >
                <option value="" disabled>카테고리 선택</option>
                <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                  {{ cat.name }}
                </option>
              </select>
            </div>

            <div>
              <label for="title" class="block text-sm font-medium text-gray-700 mb-1">제목</label>
              <input
                id="title"
                v-model="newPost.title"
                type="text"
                required
                placeholder="제목을 입력하세요"
                class="w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
              />
            </div>

            <div>
              <label for="content" class="block text-sm font-medium text-gray-700 mb-1">내용</label>
              <textarea
                id="content"
                v-model="newPost.content"
                rows="10"
                required
                placeholder="내용을 입력하세요"
                class="w-full p-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
              ></textarea>
            </div>

            <div class="flex justify-end pt-4">
              <button
                type="button"
                @click="showPostForm = false"
                class="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 mr-2 hover:bg-gray-50"
              >
                취소
              </button>
              <button
                type="submit"
                class="px-4 py-2 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700"
              >
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
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import axios from 'axios'

// Sticky offsets
const HEADER_HEIGHT = 64
const TAB_HEIGHT = 48

const router = useRouter()
const userStore = useUserStore()

const loading = ref(true)
const posts = ref([])
const categories = ref([
  { id: 'all', name: '전체' },
  { id: 'invest', name: '투자 정보' },
  { id: 'review', name: '매물 리뷰' },
  { id: 'qa', name: '질문/답변' },
  { id: 'region', name: '지역 정보' },
  { id: 'free', name: '자유 게시판' },
])
const activeCategory = ref('all')
const currentPage = ref(1)
const totalPages = ref(1)
const searchQuery = ref('')
const selectedPost = ref(null)
const showPostForm = ref(false)
const commentText = ref('')

const newPost = ref({ categoryId: '', title: '', content: '' })

onMounted(fetchPosts)
watch([activeCategory, currentPage], fetchPosts)

async function fetchPosts() {
  loading.value = true
  try {
    const res = await axios.get('http://localhost:8080/api/v1/community/posts', {
      params: {
        category: activeCategory.value === 'all' ? '' : activeCategory.value,
        search: searchQuery.value,
        page: currentPage.value - 1,
        size: 5,
      },
    })
    const { content, totalPages: tp } = res.data
    posts.value = content.map((p) => ({
      id: p.postId,
      categoryId: p.categoryId,
      title: p.title,
      content: p.content,
      authorName: p.authorName,
      authorAvatar: p.authorAvatarUrl,
      createdAt: p.createdAt,
      views: p.views,
      commentsCount: p.commentCount,
    }))
    totalPages.value = tp
  } catch (e) {
    console.error(e)
    alert('게시글을 불러오는 중 오류가 발생했습니다.')
  } finally {
    loading.value = false
  }
}

async function searchPosts() {
  currentPage.value = 1
  await fetchPosts()
}

async function openPostDetail(post) {
  try {
    const res = await axios.get(`http://localhost:8080/api/v1/community/posts/${post.id}`)
    const p = res.data
    selectedPost.value = {
      id: p.postId,
      categoryId: p.categoryId,
      title: p.title,
      content: p.content,
      authorName: p.authorName,
      authorAvatar: p.authorAvatarUrl,
      createdAt: p.createdAt,
      views: p.views,
      comments: p.comments.map((c) => ({
        id: c.commentId,
        authorName: c.authorName,
        authorAvatar: c.authorAvatarUrl,
        content: c.content,
        createdAt: c.createdAt,
      })),
    }
  } catch (e) {
    console.error(e)
    alert('상세 정보를 불러오는 중 오류가 발생했습니다.')
  }
}

function openPostForm() {
  if (!userStore.isLogged) {
    alert('로그인이 필요한 서비스입니다.')
    router.push('/login')
    return
  }
  showPostForm.value = true
  newPost.value = { categoryId: '', title: '', content: '' }
}

async function submitPost() {
  try {
    await axios.post('http://localhost:8080/api/v1/community/posts', newPost.value)
    showPostForm.value = false
    await fetchPosts()
  } catch (e) {
    console.error(e)
    alert('게시글 등록에 실패했습니다.')
  }
}

async function addComment() {
  if (!commentText.value.trim()) return
  if (!userStore.isLogged) {
    alert('로그인이 필요한 서비스입니다.')
    router.push('/login')
    return
  }
  try {
    await axios.post(
      `http://localhost:8080/api/v1/community/posts/${selectedPost.value.id}/comments`,
      { content: commentText.value },
    )
    await openPostDetail(selectedPost.value)
    commentText.value = ''
  } catch (e) {
    console.error(e)
    alert('댓글 작성에 실패했습니다.')
  }
}

function getCategoryName(categoryId) {
  const cat = categories.value.find((c) => c.id === categoryId)
  return cat ? cat.name : '기타'
}

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

function close() {
  router.back()
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  // "YYYY-MM-DDTHH:mm:ss" 또는 "YYYY-MM-DD HH:mm:ss" 형태 가정
  const [datePart, timePart] = dateStr.includes('T') ? dateStr.split('T') : dateStr.split(' ')
  if (!timePart) return datePart // 혹시 time 없는 경우

  // 초(및 밀리초) 제거, "HH:mm:ss" → ["HH", "mm", ...]
  const [hh, mm] = timePart.split(':')
  const [yyyy, MM, dd] = datePart.split('-')

  return `${yyyy}년 ${MM}월 ${dd}일 ${hh}:${mm}`
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
  line-clamp: 2;
  overflow: hidden;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style>
