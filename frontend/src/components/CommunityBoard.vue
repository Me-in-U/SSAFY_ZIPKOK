<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-60">
    <div
      class="bg-white rounded-2xl shadow-2xl w-full max-w-6xl h-[85vh] overflow-hidden relative flex flex-col"
    >
      <!-- 헤더 -->
      <div
        class="flex items-center justify-between px-8 py-6 border-b border-gray-100 sticky top-0 bg-white z-10"
        style="height: 80px"
      >
        <div class="flex items-center gap-3">
          <div
            class="w-10 h-10 bg-gradient-to-br from-emerald-500 to-teal-600 rounded-xl flex items-center justify-center"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-6 w-6 text-white"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M17 8h2a2 2 0 012 2v6a2 2 0 01-2 2h-2v4l-4-4H9a3 3 0 01-3-3v-1"
              />
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M9 12H7a2 2 0 01-2-2V4a2 2 0 012-2h10a2 2 0 012 2v6a2 2 0 01-2 2h-2v4l-4-4H9z"
              />
            </svg>
          </div>
          <h2
            class="text-2xl font-bold bg-gradient-to-r from-gray-800 to-gray-600 bg-clip-text text-transparent"
          >
            부동산 커뮤니티
          </h2>
        </div>
        <button
          @click="close"
          class="w-10 h-10 rounded-full bg-gray-100 hover:bg-gray-200 flex items-center justify-center transition-colors"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5 text-gray-500"
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

      <!-- 카테고리 탭 -->
      <div
        class="pl-8 pr-8 border-b border-gray-100 bg-gradient-to-r from-gray-50 to-white sticky z-10"
        :style="{ top: '80px' }"
      >
        <div class="flex space-x-3 overflow-x-auto pb-2 scrollbar-hide">
          <button
            v-for="cat in categories"
            :key="cat.id"
            @click="((activeCategory = cat.id), (currentPage = 1))"
            class="px-4 py-3 mt-2 ml-2 rounded-xl text-sm font-semibold whitespace-nowrap transition-all duration-200 transform hover:scale-105"
            :class="
              activeCategory === cat.id
                ? 'bg-gradient-to-r from-emerald-500 to-teal-600 text-white shadow-lg shadow-emerald-200'
                : 'bg-white text-gray-600 hover:bg-gray-50 border border-gray-200 hover:border-emerald-200'
            "
          >
            {{ cat.name }}
          </button>
        </div>
      </div>

      <!-- 검색 & 글쓰기 -->
      <div
        class="px-8 py-3 bg-white border-b border-gray-100 flex flex-wrap gap-4 items-center sticky z-10"
        :style="{ top: '140px' }"
      >
        <div class="relative flex-1 min-w-[300px]">
          <input
            v-model="searchQuery"
            @keyup.enter="searchPosts"
            type="text"
            placeholder="궁금한 내용을 검색해보세요..."
            class="w-full pl-12 pr-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all bg-gray-50 hover:bg-white"
          />
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5 absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400"
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
          class="bg-gradient-to-r from-emerald-500 to-teal-600 text-white px-6 py-3 rounded-xl hover:from-emerald-600 hover:to-teal-700 transition-all duration-200 flex items-center gap-2 font-semibold shadow-lg hover:shadow-xl transform hover:scale-105"
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
          새 글 작성
        </button>
      </div>

      <!-- 게시글 목록 -->
      <div class="flex-1 overflow-auto px-8 py-4">
        <div v-if="loading" class="flex justify-center items-center py-32">
          <div class="relative">
            <div
              class="w-16 h-16 border-4 border-emerald-200 border-t-emerald-500 rounded-full animate-spin"
            ></div>
            <div class="absolute inset-0 flex items-center justify-center">
              <div class="w-8 h-8 bg-emerald-500 rounded-full animate-pulse"></div>
            </div>
          </div>
        </div>

        <div v-else-if="posts.length === 0" class="text-center py-32">
          <div
            class="w-24 h-24 mx-auto mb-6 bg-gradient-to-br from-gray-100 to-gray-200 rounded-2xl flex items-center justify-center"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-12 w-12 text-gray-400"
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
          </div>
          <h3 class="text-xl font-semibold text-gray-600 mb-2">아직 게시글이 없어요</h3>
          <p class="text-gray-500 mb-6">첫 번째 게시글을 작성해서 대화를 시작해보세요!</p>
          <button
            @click="openPostForm"
            class="bg-gradient-to-r from-emerald-500 to-teal-600 text-white px-6 py-3 rounded-xl hover:from-emerald-600 hover:to-teal-700 transition-all font-semibold"
          >
            첫 글 작성하기
          </button>
        </div>

        <div v-else class="space-y-4">
          <div
            v-for="post in posts"
            :key="post.id"
            class="bg-white border border-gray-200 rounded-2xl px-6 py-3 hover:shadow-lg hover:border-emerald-200 transition-all duration-200 cursor-pointer group"
            @click="openPostDetail(post)"
          >
            <div class="flex justify-between items-start mb-4">
              <div class="flex-1">
                <div class="flex items-center gap-3 mb-3">
                  <span
                    class="inline-flex items-center px-3 py-1 text-xs font-medium rounded-full"
                    :class="getCategoryClass(post.categoryId)"
                  >
                    {{ getCategoryName(post.categoryId) }}
                  </span>
                  <div class="flex items-center text-xs text-gray-500 gap-4">
                    <span class="flex items-center gap-1">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-4 w-4"
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
                    <span class="flex items-center gap-1">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-4 w-4"
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
                <h3
                  class="text-lg font-semibold text-gray-800 mb-2 group-hover:text-emerald-600 transition-colors"
                >
                  {{ post.title }}
                </h3>
                <p class="text-gray-600 text-sm line-clamp-2 leading-relaxed">{{ post.content }}</p>
              </div>

              <!-- 수정/삭제 버튼 -->
              <div v-if="userStore.profile?.mno === post.authorId" class="flex gap-2 ml-4">
                <button
                  @click.stop="startEditPost(post)"
                  class="px-3 py-1.5 text-xs font-medium text-blue-600 bg-blue-50 hover:bg-blue-100 rounded-lg transition-colors"
                >
                  수정
                </button>
                <button
                  @click.stop="deletePost(post.id)"
                  class="px-3 py-1.5 text-xs font-medium text-red-600 bg-red-50 hover:bg-red-100 rounded-lg transition-colors"
                >
                  삭제
                </button>
              </div>
            </div>

            <div class="flex justify-between items-center pt-2 border-t border-gray-20">
              <div class="flex items-center gap-2">
                <div
                  class="w-8 h-8 rounded-full bg-gradient-to-br from-emerald-400 to-teal-500 flex items-center justify-center text-white text-sm font-semibold"
                >
                  {{ post.authorName?.charAt(0) || '익' }}
                </div>
                <span class="text-sm font-medium text-gray-700">{{ post.authorName }}</span>
              </div>
              <span class="text-xs text-gray-500">{{ formatDate(post.createdAt) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 페이지네이션 -->
      <div class="px-8 py-4 border-t border-gray-100 flex justify-center bg-gray-50">
        <div class="flex items-center gap-2">
          <button
            @click="currentPage > 1 && currentPage--"
            :disabled="currentPage === 1"
            class="px-4 py-2 rounded-lg border transition-colors"
            :class="
              currentPage === 1
                ? 'text-gray-400 border-gray-200 cursor-not-allowed'
                : 'text-gray-700 border-gray-300 hover:bg-white hover:border-emerald-300'
            "
          >
            이전
          </button>
          <button
            v-for="n in totalPages"
            :key="n"
            @click="currentPage = n"
            class="px-4 py-2 rounded-lg border transition-all"
            :class="
              currentPage === n
                ? 'bg-gradient-to-r from-emerald-500 to-teal-600 text-white border-emerald-500 shadow-lg'
                : 'text-gray-700 border-gray-300 hover:bg-white hover:border-emerald-300'
            "
          >
            {{ n }}
          </button>
          <button
            @click="currentPage < totalPages && currentPage++"
            :disabled="currentPage === totalPages"
            class="px-4 py-2 rounded-lg border transition-colors"
            :class="
              currentPage === totalPages
                ? 'text-gray-400 border-gray-200 cursor-not-allowed'
                : 'text-gray-700 border-gray-300 hover:bg-white hover:border-emerald-300'
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
      class="fixed inset-0 z-[60] flex items-center justify-center p-4 bg-black bg-opacity-60 backdrop-blur-sm"
    >
      <div
        class="bg-white rounded-2xl shadow-2xl w-full max-w-4xl max-h-[90vh] overflow-hidden relative flex flex-col"
      >
        <div
          class="sticky top-0 bg-white z-10 border-b border-gray-100 px-8 py-6 flex justify-between items-center"
        >
          <div class="flex-1">
            <div class="flex items-center gap-3 mb-2">
              <span
                class="inline-flex items-center px-3 py-1 text-xs font-medium rounded-full"
                :class="getCategoryClass(selectedPost.categoryId)"
              >
                {{ getCategoryName(selectedPost.categoryId) }}
              </span>
            </div>
            <h3 class="text-2xl font-bold text-gray-800">{{ selectedPost.title }}</h3>
          </div>

          <div class="flex items-center gap-3">
            <!-- 수정/삭제 버튼 -->
            <div v-if="userStore.profile?.mno === selectedPost.authorId" class="flex gap-2">
              <button
                @click="startEditPost(selectedPost)"
                class="px-4 py-2 text-sm font-medium text-blue-600 bg-blue-50 hover:bg-blue-100 rounded-lg transition-colors"
              >
                수정
              </button>
              <button
                @click="deletePost(selectedPost.id)"
                class="px-4 py-2 text-sm font-medium text-red-600 bg-red-50 hover:bg-red-100 rounded-lg transition-colors"
              >
                삭제
              </button>
            </div>
            <button
              @click="selectedPost = null"
              class="w-10 h-10 rounded-full bg-gray-100 hover:bg-gray-200 flex items-center justify-center transition-colors"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5 text-gray-500"
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
        </div>

        <div class="flex-1 overflow-auto p-8">
          <div class="flex items-center gap-3 mb-6 pb-6 border-b border-gray-100">
            <div
              class="w-12 h-12 rounded-full bg-gradient-to-br from-emerald-400 to-teal-500 flex items-center justify-center text-white text-lg font-semibold"
            >
              {{ selectedPost.authorName?.charAt(0) || '익' }}
            </div>
            <div>
              <p class="font-semibold text-gray-800">{{ selectedPost.authorName }}</p>
              <p class="text-sm text-gray-500">{{ formatDate(selectedPost.createdAt) }}</p>
            </div>
          </div>

          <div class="prose max-w-none mb-8">
            <p class="text-gray-700 leading-relaxed whitespace-pre-wrap">
              {{ selectedPost.content }}
            </p>
          </div>

          <!-- 댓글 섹션 -->
          <div class="border-t border-gray-100 pt-8">
            <h4 class="text-xl font-semibold text-gray-800 mb-6 flex items-center gap-2">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-6 w-6 text-emerald-500"
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
              댓글 {{ selectedPost.comments?.length || 0 }}개
            </h4>

            <!-- 댓글 목록 -->
            <div class="space-y-4 mb-8">
              <div v-for="comment in comments" :key="comment.id" class="bg-gray-50 rounded-xl p-6">
                <div class="flex justify-between items-start mb-3">
                  <div class="flex items-center gap-3">
                    <div
                      class="w-10 h-10 rounded-full bg-gradient-to-br from-gray-400 to-gray-500 flex items-center justify-center text-white text-sm font-semibold"
                    >
                      {{ comment.authorName?.charAt(0) || '익' }}
                    </div>
                    <div>
                      <p class="font-semibold text-gray-800">{{ comment.authorName }}</p>
                      <p class="text-xs text-gray-500">{{ formatDate(comment.createdAt) }}</p>
                    </div>
                  </div>

                  <!-- 댓글 수정/삭제 버튼 -->
                  <div v-if="userStore.profile?.mno === comment.authorId" class="flex gap-2">
                    <button
                      @click="startEditComment(comment)"
                      class="px-3 py-1 text-xs font-medium text-blue-600 bg-blue-100 hover:bg-blue-200 rounded-lg transition-colors"
                    >
                      수정
                    </button>
                    <button
                      @click="deleteComment(comment.id)"
                      class="px-3 py-1 text-xs font-medium text-red-600 bg-red-100 hover:bg-red-200 rounded-lg transition-colors"
                    >
                      삭제
                    </button>
                  </div>
                </div>

                <!-- 댓글 수정 모드 -->
                <div v-if="editingCommentId === comment.id" class="space-y-3">
                  <textarea
                    v-model="editCommentText"
                    class="w-full p-4 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent resize-none"
                    rows="3"
                  ></textarea>
                  <div class="flex justify-end gap-2">
                    <button
                      @click="cancelEditComment"
                      class="px-4 py-2 text-sm font-medium text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
                    >
                      취소
                    </button>
                    <button
                      @click="saveEditComment(comment.id)"
                      class="px-4 py-2 text-sm font-medium text-white bg-gradient-to-r from-emerald-500 to-teal-600 hover:from-emerald-600 hover:to-teal-700 rounded-lg transition-all"
                    >
                      저장
                    </button>
                  </div>
                </div>
                <p v-else class="text-gray-700 leading-relaxed">{{ comment.content }}</p>
              </div>
            </div>

            <!-- 새 댓글 작성 -->
            <div class="bg-white border border-gray-200 rounded-xl p-6">
              <h5 class="font-semibold text-gray-800 mb-4">댓글 작성</h5>
              <textarea
                v-model="commentText"
                class="w-full p-4 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent resize-none"
                placeholder="댓글을 입력해주세요..."
                rows="4"
              ></textarea>
              <div class="flex justify-end mt-4">
                <button
                  @click="addComment"
                  class="px-6 py-3 text-sm font-semibold text-white bg-gradient-to-r from-emerald-500 to-teal-600 hover:from-emerald-600 hover:to-teal-700 rounded-xl transition-all shadow-lg hover:shadow-xl"
                >
                  댓글 등록
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 글쓰기/수정 모달 -->
    <div
      v-if="showPostForm"
      class="fixed inset-0 z-[60] flex items-center justify-center p-4 bg-black bg-opacity-60 backdrop-blur-sm"
    >
      <div
        class="bg-white rounded-2xl shadow-2xl w-full max-w-4xl max-h-[90vh] overflow-hidden relative flex flex-col"
      >
        <div
          class="sticky top-0 bg-white z-10 border-b border-gray-100 px-8 py-6 flex justify-between items-center"
        >
          <h3 class="text-2xl font-bold text-gray-800">
            {{ editingPost ? '게시글 수정' : '새 게시글 작성' }}
          </h3>
          <button
            @click="cancelEditPost"
            class="w-10 h-10 rounded-full bg-gray-100 hover:bg-gray-200 flex items-center justify-center transition-colors"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-5 w-5 text-gray-500"
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

        <div class="flex-1 overflow-auto p-8">
          <form @submit.prevent="submitPost" class="space-y-6">
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-3">카테고리</label>
              <select
                v-model="newPost.categoryId"
                required
                class="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent"
              >
                <option value="">카테고리를 선택해주세요</option>
                <option
                  v-for="cat in categories.filter((c) => c.id !== 'all')"
                  :key="cat.id"
                  :value="cat.id"
                >
                  {{ cat.name }}
                </option>
              </select>
            </div>

            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-3">제목</label>
              <input
                v-model="newPost.title"
                type="text"
                required
                placeholder="제목을 입력해주세요"
                class="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent"
              />
            </div>

            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-3">내용</label>
              <textarea
                v-model="newPost.content"
                rows="9"
                required
                placeholder="내용을 입력해주세요"
                class="w-full p-4 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent resize-none"
              ></textarea>
            </div>

            <div class="flex justify-end gap-4 pt-3 border-t border-gray-200">
              <button
                type="button"
                @click="cancelEditPost"
                class="px-6 py-3 text-sm font-semibold text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-xl transition-colors"
              >
                취소
              </button>
              <button
                type="submit"
                class="px-8 py-3 text-sm font-semibold text-white bg-gradient-to-r from-emerald-500 to-teal-600 hover:from-emerald-600 hover:to-teal-700 rounded-xl transition-all shadow-lg hover:shadow-xl"
              >
                {{ editingPost ? '수정하기' : '등록하기' }}
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

const router = useRouter()
const userStore = useUserStore()

// 상태
const loading = ref(false)
const posts = ref([])
const comments = ref([])
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
const editingPost = ref(null)
const newPost = ref({ categoryId: '', title: '', content: '' })
const commentText = ref('')
const editingCommentId = ref(null)
const editCommentText = ref('')

// 목록 & 페이징
async function fetchPosts() {
  loading.value = true
  try {
    const res = await axios.get('https://api.ssafy.blog/api/v1/community/posts', {
      params: {
        category: activeCategory.value === 'all' ? '' : activeCategory.value,
        search: searchQuery.value,
        page: currentPage.value - 1,
        size: 5,
      },
    })

    let content, tp
    if (Array.isArray(res.data)) {
      content = res.data
      tp = 1
    } else {
      content = res.data.content || []
      tp = res.data.totalPages || 1
    }

    posts.value = content.map((p) => ({
      id: p.postId,
      categoryId: p.categoryId,
      title: p.title,
      content: p.content,
      authorName: p.authorName,
      authorId: p.authorId,
      authorAvatar: p.authorAvatarUrl,
      createdAt: p.createdAt,
      views: p.views,
      commentsCount: p.commentCount,
    }))
    totalPages.value = tp
  } catch (e) {
    console.error('fetchPosts error:', e)
    alert('목록 조회 실패: ' + (e.response?.statusText || e.message))
  } finally {
    loading.value = false
  }
}

// 상세 & 댓글
async function openPostDetail(post) {
  const { data: p } = await axios.get(`https://api.ssafy.blog/api/v1/community/posts/${post.id}`)
  selectedPost.value = { ...p, id: p.postId }
  comments.value = p.comments.map((c) => ({
    id: c.commentId,
    authorId: c.authorId,
    authorName: c.authorName,
    content: c.content,
    createdAt: c.createdAt,
  }))
}

// 댓글 CRUD
async function addComment() {
  if (!commentText.value.trim()) return
  await axios.post(
    `https://api.ssafy.blog/api/v1/community/posts/${selectedPost.value.id}/comments`,
    { content: commentText.value },
  )
  commentText.value = ''
  await openPostDetail(selectedPost.value)
}

function startEditComment(c) {
  editingCommentId.value = c.id
  editCommentText.value = c.content
}

function cancelEditComment() {
  editingCommentId.value = null
  editCommentText.value = ''
}

async function saveEditComment(id) {
  if (!editCommentText.value.trim()) return
  await axios.put(
    `https://api.ssafy.blog/api/v1/community/posts/${selectedPost.value.id}/comments/${id}`,
    { content: editCommentText.value },
  )
  cancelEditComment()
  await openPostDetail(selectedPost.value)
}

async function deleteComment(id) {
  if (!confirm('댓글을 삭제하시겠습니까?')) return
  await axios.delete(
    `https://api.ssafy.blog/api/v1/community/posts/${selectedPost.value.id}/comments/${id}`,
  )
  await openPostDetail(selectedPost.value)
}

// 글쓰기/수정 모달
function openPostForm() {
  if (!userStore.isLogged) {
    alert('로그인이 필요합니다')
    router.push('/login')
    return
  }
  editingPost.value = null
  newPost.value = { categoryId: '', title: '', content: '' }
  showPostForm.value = true
}

function startEditPost(post) {
  editingPost.value = post
  newPost.value = { categoryId: post.categoryId, title: post.title, content: post.content }
  showPostForm.value = true
  selectedPost.value = null
}

function cancelEditPost() {
  editingPost.value = null
  showPostForm.value = false
  newPost.value = { categoryId: '', title: '', content: '' }
}

async function submitPost() {
  if (editingPost.value) {
    await axios.put(
      `https://api.ssafy.blog/api/v1/community/posts/${editingPost.value.id}`,
      newPost.value,
    )
  } else {
    await axios.post('https://api.ssafy.blog/api/v1/community/posts', newPost.value)
  }
  cancelEditPost()
  await fetchPosts()
}
async function deletePost(id) {
  if (!confirm('게시글을 삭제하시겠습니까?')) return
  try {
    await axios.delete(`https://api.ssafy.blog/api/v1/community/posts/${id}`)
    // 상세 모달이 열려 있었다면 닫아주기
    if (selectedPost.value?.id === id) {
      selectedPost.value = null
    }
    await fetchPosts()
  } catch (e) {
    console.error(e)
    alert('삭제에 실패했습니다.')
  }
}
// 검색 & 페이징
async function searchPosts() {
  currentPage.value = 1
  await fetchPosts()
}

watch([activeCategory, currentPage], fetchPosts)
onMounted(fetchPosts)

// 유틸
function getCategoryName(id) {
  return categories.value.find((c) => c.id === id)?.name || '기타'
}

function getCategoryClass(id) {
  switch (id) {
    case 'invest':
      return 'bg-blue-100 text-blue-700 border border-blue-200'
    case 'review':
      return 'bg-purple-100 text-purple-700 border border-purple-200'
    case 'qa':
      return 'bg-yellow-100 text-yellow-700 border border-yellow-200'
    case 'region':
      return 'bg-green-100 text-green-700 border border-green-200'
    case 'free':
      return 'bg-pink-100 text-pink-700 border border-pink-200'
    default:
      return 'bg-gray-100 text-gray-700 border border-gray-200'
  }
}

function formatDate(dt) {
  // dt가 없거나 문자열이 아니면 빈 문자열 반환
  if (!dt || typeof dt !== 'string') return ''

  // "YYYY-MM-DDTHH:mm:ss" 또는 "YYYY-MM-DD HH:mm:ss" 모두 처리
  const [datePart, timePart = ''] = dt.includes('T') ? dt.split('T') : dt.split(' ')

  const [y, m, d] = datePart.split('-')
  const [hh = '00', mm = '00'] = timePart.split(':')

  return `${y}년 ${m}월 ${d}일 ${hh}:${mm}`
}

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

.prose {
  max-width: none;
}

.prose p {
  margin-bottom: 1rem;
}
</style>
