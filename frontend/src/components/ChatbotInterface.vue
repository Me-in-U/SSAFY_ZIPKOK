<template>
  <div class="h-full bg-white rounded-lg shadow flex flex-col">
    <!-- 헤더 -->
    <div class="p-4 border-b flex flex-row items-center justify-between">
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
        <button @click="isMinimized = !isMinimized">
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

    <div v-if="!isMinimized" class="flex-1 flex flex-col">
      <!-- 메시지 영역 -->
      <div class="flex-1 overflow-auto p-4 space-y-4" ref="messagesContainer">
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
            <p>{{ message.content }}</p>
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

      <!-- 추천 질문 (처음에만 표시) -->
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

      <!-- 입력 영역 -->
      <div class="p-4 border-t">
        <div class="flex w-full items-center gap-2">
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

// 추천 질문
const suggestedQuestions = [
  '서울 강남 아파트 시세 어때?',
  '투자하기 좋은 지역 추천해줘',
  '전세 vs 월세 어떤 게 투자에 유리해?',
  '부동산 투자 초보자 팁 알려줘',
  '지금이 매수 타이밍일까?',
]

// 봇 응답 목록
const botResponses = [
  {
    keywords: ['강남', '시세'],
    response:
      '현재 서울 강남 지역의 아파트 시세는 평균 3.2% 상승했습니다. 특히 압구정, 청담 지역은 재건축 이슈로 5% 이상 상승했습니다. 더 자세한 정보가 필요하신가요?',
    options: ['재건축 단지 정보 알려줘', '강남 투자 추천 매물 보여줘'],
  },
  {
    keywords: ['투자', '추천', '지역'],
    response:
      '현재 투자 수익률이 높은 지역으로는 판교, 송파, 하남 등이 있습니다. 특히 교통 인프라 개발이 예정된 하남시는 향후 가치 상승이 기대됩니다. 특정 지역에 관심이 있으신가요?',
    options: ['판교 매물 보여줘', '하남 개발 계획 자세히'],
  },
  {
    keywords: ['전세', '월세'],
    response:
      '전세와 월세는 투자 목적에 따라 선택이 달라집니다. 전세는 초기 자본이 많이 필요하지만 안정적이고, 월세는 현금 흐름이 중요한 투자자에게 적합합니다. 현재 시장에서는 월세 수익률이 평균 4~5%로 전세 대비 유리한 편입니다.',
    options: ['월세 수익률 높은 매물 추천', '전세 대출 조건 알려줘'],
  },
  {
    keywords: ['초보', '팁', '조언'],
    response:
      '부동산 투자 초보자라면 다음 사항을 고려하세요: 1) 교통과 편의시설이 좋은 입지 선택 2) 총 자산의 30% 이상 투자하지 않기 3) 대출 상환 계획 세우기 4) 세금 및 관리비 계산하기 5) 장기적 관점으로 접근하기. 더 구체적인 조언이 필요하신가요?',
    options: ['입지 선택 팁 더 알려줘', '대출 관련 조언 부탁해'],
  },
  {
    keywords: ['매수', '타이밍'],
    response:
      '부동산 시장은 현재 금리 인상과 정부 정책 변화로 조정기에 있습니다. 하지만 지역과 매물에 따라 상황이 다릅니다. 장기 투자 관점에서는 실수요 지역의 우량 매물은 지금도 좋은 매수 기회가 될 수 있습니다. 특정 지역에 관심이 있으신가요?',
    options: ['실수요 지역 추천해줘', '금리 전망은 어때?'],
  },
  {
    keywords: ['시장', '동향'],
    response:
      '최근 부동산 시장은 금리 상승으로 인해 전반적으로 거래량이 감소했습니다. 그러나 역세권, 학군이 좋은 지역, 재개발 예정 지역은 여전히 수요가 있습니다. 특히 서울 외곽과 신도시 지역의 가격 조정폭이 커서 투자 기회가 생기고 있습니다.',
    options: ['재개발 지역 알려줘', '신도시 투자 전망은?'],
  },
]

// 메시지 전송
function sendMessage() {
  if (!inputMessage.value.trim()) return

  // 사용자 메시지 추가
  messages.value.push({
    content: inputMessage.value,
    sender: 'user',
    timestamp: new Date(),
  })

  const userMessage = inputMessage.value
  inputMessage.value = ''

  // 타이핑 효과
  isTyping.value = true

  // 봇 응답 생성 (실제로는 AI API 호출)
  setTimeout(() => {
    isTyping.value = false

    // 키워드 기반 응답 찾기
    const userMessageLower = userMessage.toLowerCase()
    let foundResponse = null

    for (const response of botResponses) {
      if (response.keywords.some((keyword) => userMessageLower.includes(keyword))) {
        foundResponse = response
        break
      }
    }

    // 적절한 응답이 없으면 기본 응답
    if (!foundResponse) {
      foundResponse = {
        response:
          '해당 질문에 대한 정보를 찾아보겠습니다. 부동산 투자와 관련하여 더 구체적인 질문이 있으시면 말씀해주세요.',
        options: ['투자 추천 매물 보여줘', '요즘 부동산 시장 어때?', '투자 수익률 높은 지역은?'],
      }
    }

    messages.value.push({
      content: foundResponse.response,
      sender: 'bot',
      timestamp: new Date(),
      options: foundResponse.options,
    })

    // 스크롤 맨 아래로
    scrollToBottom()
  }, 1500)
}

// 빠른 응답
function quickReply(question) {
  inputMessage.value = question
  sendMessage()
}

// 채팅 내역 지우기
function clearChat() {
  messages.value = [
    {
      content: '안녕하세요! 부동산 투자 AI 어시스턴트입니다. 어떤 도움이 필요하신가요?',
      sender: 'bot',
      options: ['투자 추천 매물 보여줘', '요즘 부동산 시장 어때?', '투자 수익률 높은 지역은?'],
    },
  ]
}

// 메시지 추가될 때마다 스크롤 아래로
watch(messages, () => {
  nextTick(() => {
    scrollToBottom()
  })
})

function scrollToBottom() {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}
</script>
