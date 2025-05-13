<template>
  <div class="h-full flex flex-col bg-white rounded-lg shadow">
    <!-- 헤더 -->
    <div class="p-4 border-b flex items-center justify-between flex-none">
      <div class="flex items-center">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
          class="text-emerald-600 mr-2"
        >
          <path d="M12 8V4H8" />
          <rect width="16" height="12" x="4" y="8" rx="2" />
          <path d="M2 14h2" />
          <path d="M20 14h2" />
          <path d="M15 13v2" />
          <path d="M9 13v2" />
        </svg>
        <h3 class="font-medium">부동산 투자 AI 챗봇</h3>
      </div>
      <div class="flex items-center gap-2">
        <button
          class="p-1 text-gray-500 hover:text-gray-700 rounded-full hover:bg-gray-100"
          @click="clearChat"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="18"
            height="18"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d="M3 6h18" />
            <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6" />
            <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2" />
            <line x1="10" x2="10" y1="11" y2="17" />
            <line x1="14" x2="14" y1="11" y2="17" />
          </svg>
        </button>
        <button @click="isMinimized = !isMinimized" class="flex-1 flex flex-col">
          <svg
            v-if="isMinimized"
            xmlns="http://www.w3.org/2000/svg"
            width="18"
            height="18"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="text-gray-500"
          >
            <polyline points="15 3 21 3 21 9" />
            <polyline points="9 21 3 21 3 15" />
            <line x1="21" x2="14" y1="3" y2="10" />
            <line x1="3" x2="10" y1="21" y2="14" />
          </svg>
          <svg
            v-else
            xmlns="http://www.w3.org/2000/svg"
            width="18"
            height="18"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="text-gray-500"
          >
            <polyline points="4 14 10 14 10 20" />
            <polyline points="20 10 14 10 14 4" />
            <line x1="14" x2="21" y1="10" y2="3" />
            <line x1="3" x2="10" y1="21" y2="14" />
          </svg>
        </button>
      </div>
    </div>

    <!-- 메시지 영역 -->
    <div v-if="!isMinimized" class="flex-1 flex flex-col overflow-hidden">
      <div
        ref="messagesContainer"
        class="flex-1 overflow-y-auto min-h-0 p-4 space-y-4 bg-gray-50 scroll-smooth"
      >
        <div
          v-for="(message, index) in messages"
          :key="index"
          class="flex"
          :class="{
            'justify-end': message.sender === 'user',
            'justify-start': message.sender === 'bot',
          }"
        >
          <div
            class="max-w-[80%] rounded-lg px-4 py-2"
            :class="{
              'bg-emerald-600 text-white': message.sender === 'user',
              'bg-gray-100 text-gray-800': message.sender === 'bot',
            }"
          >
            <div class="flex items-center gap-2 mb-1">
              <svg
                v-if="message.sender === 'bot'"
                xmlns="http://www.w3.org/2000/svg"
                width="16"
                height="16"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
                class="text-emerald-600"
              >
                <path d="M12 8V4H8" />
                <rect width="16" height="12" x="4" y="8" rx="2" />
                <path d="M2 14h2" />
                <path d="M20 14h2" />
                <path d="M15 13v2" />
                <path d="M9 13v2" />
              </svg>
              <svg
                v-else
                xmlns="http://www.w3.org/2000/svg"
                width="16"
                height="16"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
                class="text-white"
              >
                <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2" />
                <circle cx="12" cy="7" r="4" />
              </svg>
              <span class="text-xs">{{ message.sender === 'user' ? '나' : 'AI 어시스턴트' }}</span>
            </div>

            <!-- Bot 또는 사용자 메시지 구분 -->
            <BotTypingMessage
              v-if="message.sender === 'bot'"
              :text="message.content"
              @done="scrollToBottom"
            />

            <p v-else>{{ message.content }}</p>

            <div
              v-if="message.sender === 'bot' && message.options && message.options.length > 0"
              class="mt-2 flex flex-wrap gap-2"
            >
              <button
                v-for="(option, optIndex) in message.options"
                :key="optIndex"
                class="px-3 py-1 text-xs bg-white text-emerald-600 border border-emerald-600 rounded-full hover:bg-emerald-50"
                @click="quickReply(option)"
              >
                {{ option }}
              </button>
            </div>
          </div>
        </div>

        <div v-if="isTyping" class="flex justify-start">
          <div class="bg-gray-100 rounded-lg px-4 py-2 max-w-[80%]">
            <div class="flex space-x-1">
              <div class="w-2 h-2 bg-gray-400 rounded-full animate-bounce"></div>
              <div class="w-2 h-2 bg-gray-400 rounded-full animate-bounce"></div>
              <div class="w-2 h-2 bg-gray-400 rounded-full animate-bounce"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- 추천 질문 -->
      <div v-if="messages.length <= 1 && !isTyping" class="px-4 py-2 border-t">
        <p class="text-sm text-gray-500 mb-2">추천 질문:</p>
        <div class="flex flex-wrap gap-2">
          <button
            v-for="(question, index) in suggestedQuestions"
            :key="index"
            class="px-3 py-1 text-xs bg-gray-100 rounded-full hover:bg-gray-200"
            @click="quickReply(question)"
          >
            {{ question }}
          </button>
        </div>
      </div>

      <!-- 입력창 -->
      <div class="p-4 border-t flex-none bg-white">
        <div class="flex items-center gap-2">
          <input
            type="text"
            placeholder="질문을 입력하세요..."
            class="flex-1 p-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-emerald-500"
            v-model="inputMessage"
            @keyup.enter="sendMessage"
          />
          <button
            class="p-2 bg-emerald-600 text-white rounded-md hover:bg-emerald-700 transition"
            @click="sendMessage"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="20"
              height="20"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <path d="m22 2-7 20-4-9-9-4Z" />
              <path d="M22 2 11 13" />
            </svg>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import BotTypingMessage from './BotTypingMessage.vue'

const isMinimized = ref(false)
const inputMessage = ref('')
const messages = ref([
  {
    content: '안녕하세요! 부동산 투자 AI 어시스턴트입니다. 어떤 도움이 필요하신가요?',
    sender: 'bot',
    options: ['투자 추천 매물 보여줘', '요즘 부동산 시장 어때?', '투자 수익률 높은 지역은?'],
  },
])
const isTyping = ref(false)
const messagesContainer = ref(null)

const suggestedQuestions = [
  '서울 강남 아파트 시세 어때?',
  '투자하기 좋은 지역 추천해줘',
  '전세 vs 월세 어떤 게 투자에 유리해?',
  '부동산 투자 초보자 팁 알려줘',
  '지금이 매수 타이밍일까?',
]

const botResponses = [
  {
    keywords: ['강남', '시세'],
    response: '현재 서울 강남 지역의 아파트 시세는 평균 3.2% 상승했습니다...',
    options: ['재건축 단지 정보 알려줘', '강남 투자 추천 매물 보여줘'],
  },
  {
    keywords: ['투자', '추천', '지역'],
    response: '현재 투자 수익률이 높은 지역으로는 판교, 송파, 하남 등이 있습니다...',
    options: ['판교 매물 보여줘', '하남 개발 계획 자세히'],
  },
  {
    keywords: ['전세', '월세'],
    response: '전세와 월세는 투자 목적에 따라 선택이 달라집니다...',
    options: ['월세 수익률 높은 매물 추천', '전세 대출 조건 알려줘'],
  },
  {
    keywords: ['초보', '팁', '조언'],
    response: '부동산 투자 초보자라면 다음 사항을 고려하세요...',
    options: ['입지 선택 팁 더 알려줘', '대출 관련 조언 부탁해'],
  },
  {
    keywords: ['매수', '타이밍'],
    response: '부동산 시장은 현재 금리 인상과 정부 정책 변화로 조정기에 있습니다...',
    options: ['실수요 지역 추천해줘', '금리 전망은 어때?'],
  },
  {
    keywords: ['시장', '동향'],
    response: '최근 부동산 시장은 금리 상승으로 인해 전반적으로 거래량이 감소했습니다...',
    options: ['재개발 지역 알려줘', '신도시 투자 전망은?'],
  },
]

function sendMessage() {
  if (!inputMessage.value.trim()) return

  messages.value.push({
    content: inputMessage.value,
    sender: 'user',
    timestamp: new Date(),
  })

  nextTick(scrollToBottom)

  const userText = inputMessage.value
  inputMessage.value = ''
  isTyping.value = true

  setTimeout(() => {
    isTyping.value = false

    const lower = userText.toLowerCase()
    let match = botResponses.find((r) => r.keywords.some((k) => lower.includes(k)))

    if (!match) {
      match = {
        response:
          '해당 질문에 대한 정보를 찾아보겠습니다. 부동산 투자와 관련하여 더 구체적인 질문이 있으시면 말씀해주세요.',
        options: ['투자 추천 매물 보여줘', '요즘 부동산 시장 어때?', '투자 수익률 높은 지역은?'],
      }
    }

    messages.value.push({
      content: match.response,
      sender: 'bot',
      timestamp: new Date(),
      options: match.options,
    })

    nextTick(scrollToBottom)
  }, 1000)
}

function quickReply(text) {
  inputMessage.value = text
  sendMessage()
}

function clearChat() {
  messages.value = [
    {
      content: '안녕하세요! 부동산 투자 AI 어시스턴트입니다. 어떤 도움이 필요하신가요?',
      sender: 'bot',
      options: ['투자 추천 매물 보여줘', '요즘 부동산 시장 어때?', '투자 수익률 높은 지역은?'],
    },
  ]
}

watch(messages, () => {
  nextTick(scrollToBottom)
})

function scrollToBottom() {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}
</script>
