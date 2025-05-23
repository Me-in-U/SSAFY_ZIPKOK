<template>
  <!-- 헤더 -->
  <div v-bind="$attrs" class="chatbot-root flex flex-col h-full">
    <!-- ...기존 헤더, 메시지, 입력창 등 코드... -->
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
      </div>
    </div>

    <!-- 메시지 영역 -->
    <div v-if="!isMinimized" class="flex-1 flex flex-col overflow-hidden">
      <div
        ref="messagesContainer"
        :key="containerKey"
        class="flex-1 overflow-y-auto min-h-0 p-4 space-y-4 bg-gray-50 scroll-smooth"
        :class="{ burn: isClearing, ignite: isAppearing }"
        @animationend="handleAnimationEnd"
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
            class="max-w-[88%] rounded-lg px-4 py-2"
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
              v-if="!message.showOptions && message.sender === 'bot'"
              :text="message.content"
              @done="onBotDone(index)"
            />

            <!-- 메시지 내용 + 추천 질문 버튼 -->
            <template v-else>
              <p>{{ message.content }}</p>
              <!-- 버튼 렌더링 부분을 아래처럼 바꿔주세요 -->
              <div
                v-if="message.options?.length && message.showOptions"
                class="mt-2 flex flex-wrap gap-2"
              >
                <transition-group appear name="stagger" tag="div" class="flex flex-wrap gap-2">
                  <button
                    v-for="(opt, i) in message.options"
                    :key="i"
                    class="px-3 py-1 text-xs bg-white text-emerald-600 border rounded-lg hover:bg-emerald-50 transform"
                    @click="quickReply(opt)"
                    :style="{ transitionDelay: `${i * 100}ms` }"
                  >
                    {{ i + 1 }}. {{ opt }}
                  </button>
                </transition-group>
              </div>
            </template>
          </div>
        </div>

        <div v-if="isTyping" class="flex justify-start">
          <div class="bg-gray-100 rounded-lg px-4 py-2 max-w-[80%]">
            <div class="flex space-x-1">
              <span class="dot"></span>
              <span class="dot"></span>
              <span class="dot"></span>
            </div>
          </div>
        </div>
      </div>

      <!-- 입력창 -->
      <div class="flex-none w-full relative">
        <div class="p-4 border-t flex-none bg-white relative z-10">
          <div class="flex items-center gap-2">
            <input
              type="text"
              placeholder="질문을 입력하세요..."
              class="flex-1 min-w-0 p-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-emerald-500"
              v-model="inputMessage"
              @keyup.enter="sendMessage"
            />
            <button
              class="flex-none p-2 bg-emerald-600 text-white rounded-md hover:bg-emerald-700 transition"
              @click="sendMessage"
              title="메시지 전송"
            >
              <!-- 검색 아이콘 -->
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
  </div>
</template>

<script setup>
import { ref, watch, nextTick, onMounted, computed } from 'vue'
import BotTypingMessage from './BotTypingMessage.vue'

// emit props
const emit = defineEmits(['search-gpt'])

// refs
const isMinimized = ref(false)
const inputMessage = ref('')
const messages = ref([])
const isTyping = ref(false)
const messagesContainer = ref(null)
const isClearing = ref(false)
const isAppearing = ref(false)
const CurrentConvoId = ref(null)
const containerKey = computed(() => {
  if (isClearing.value) return 'clear'
  if (isAppearing.value) return 'appear'
  return 'idle'
})
// onMounted
onMounted(() => {
  // 첫 렌더링 시 기본 인삿말 세팅
  messages.value = [
    {
      content: '안녕하세요! 부동산 투자 AI 어시스턴트입니다. 어떤 도움이 필요하신가요?',
      sender: 'bot',
      options: ['투자 추천 매물 보여줘', '요즘 부동산 시장 어때?', '투자 수익률 높은 지역은?'],
      showOptions: true,
    },
  ]
})

// watch
watch(messages, () => {
  nextTick(scrollToBottom)
})

async function sendMessage() {
  const userText = inputMessage.value.trim()
  if (!userText) return

  // SQL 인젝션 위험 문자 제거 (예: ', "; --)
  const sanitizedText = userText.replace(/(--|['";])/g, '')

  // 사용자 입력 메시지 추가
  messages.value.push({ content: sanitizedText, sender: 'user' })
  inputMessage.value = ''
  nextTick(scrollToBottom)
  isTyping.value = true

  try {
    // API 호출
    const res = await fetch('http://localhost:8080/ai/user-controlled', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ message: sanitizedText, convoId: CurrentConvoId.value }),
    })

    if (!res.ok) {
      throw new Error(`서버 응답 실패: ${res.status} ${res.statusText}`)
    }

    // 결과 분해: 이제 ChatResponseDto 에는 message + aptSeqList 가 옵니다.
    const result = await res.json()
    console.log('[Chat Result]', result)
    const { message, aptSeqList, relatedQuestionList, convoId } = result
    CurrentConvoId.value = convoId
    // 채팅에는 message 만 보여주기
    messages.value.push({
      content: message,
      sender: 'bot',
      options: relatedQuestionList || [],
      showOptions: false,
    })

    // 부모(App.vue)로 검색 결과 전달
    if (aptSeqList.length > 0) {
      emit('search-gpt', aptSeqList || [])
    }
  } catch (error) {
    console.error('[Chat Error]', error)
    messages.value.push({
      content: '❗ 서버 오류가 발생했습니다. 콘솔을 확인해주세요.',
      sender: 'bot',
    })
  } finally {
    isTyping.value = false
    nextTick(scrollToBottom)
  }
}
function onBotDone(idx) {
  messages.value[idx].showOptions = true
  nextTick(scrollToBottom)
}

function clearChat() {
  isClearing.value = true
}
// 애니메이션 end 이벤트 통합 핸들러
function handleAnimationEnd(evt) {
  // console.log(
  //   '[AnimationEnd] name=',
  //   evt.animationName,
  //   'isClearing=',
  //   isClearing.value,
  //   'isAppearing=',
  //   isAppearing.value,
  // )
  // 'burn' 애니 종료
  if (evt.animationName.startsWith('burn') && isClearing.value) {
    // console.log('[burn 끝 → 초기화]')
    isClearing.value = false
    // 메시지 초기화
    messages.value = [
      {
        content: '안녕하세요! 부동산 투자 AI 어시스턴트입니다. 어떤 도움이 필요하신가요?',
        sender: 'bot',
        options: ['투자 추천 매물 보여줘', '요즘 부동산 시장 어때?', '투자 수익률 높은 지역은?'],
        showOptions: true,
      },
    ]

    // 들어오는 애니 트리거
    nextTick(() => {
      // console.log('[init 후 ignite 트리거]')
      isAppearing.value = true
    })
    return
  }

  // 'ignite' 애니 종료
  if (evt.animationName.startsWith('ignite') && isAppearing.value) {
    // console.log('[ignite 끝 → 플래그 리셋]')
    isAppearing.value = false
  }
}

function quickReply(text) {
  inputMessage.value = text
  sendMessage()
}

function scrollToBottom() {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTo({
      top: messagesContainer.value.scrollHeight,
      behavior: 'smooth',
    })
  }
}
</script>
<style scoped>
/* 봇 대답 로딩 애니메이션 */
@keyframes bounceDot {
  0%,
  80%,
  100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-6px);
  }
}

.dot {
  width: 0.5rem; /* 8px */
  height: 0.5rem;
  background-color: #9ca3af; /* gray-400 */
  border-radius: 9999px; /* rounded-full */
  display: inline-block;
  animation: bounceDot 0.8s infinite ease-in-out;
}

/* 순서마다 딜레이 줘서 파도타기 효과 */
.dot:nth-child(1) {
  animation-delay: 1s;
}
.dot:nth-child(2) {
  animation-delay: 2s;
}
.dot:nth-child(3) {
  animation-delay: 3s;
}

/*추천 질문 버튼 애니메이션 */
/* enter-from: 초기 상태 (투명하고 아래에서 시작) */
.stagger-enter-from {
  opacity: 0;
  transform: translateY(10px);
}
/* enter-active: 전환 효과 */
.stagger-enter-active {
  transition:
    opacity 0.3s ease,
    transform 0.3s ease;
}
/* enter-to: 최종 상태 (보통 클래스 없어도 됨) */
.stagger-enter-to {
  opacity: 1;
  transform: translateY(0);
}
/* enter- 시리즈는 이미 있으니, appear- 시리즈 복사 */
.stagger-appear-from {
  opacity: 0;
  transform: translateY(10px);
}
.stagger-appear-active {
  transition:
    opacity 0.3s ease,
    transform 0.3s ease;
}
.stagger-appear-to {
  opacity: 1;
  transform: translateY(0);
}

/* ✨ 불타는 애니메이션 CSS (style scoped) */
@keyframes burn {
  0% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.6;
    transform: scale(0.9);
    filter: hue-rotate(0deg) blur(20px);
  }
  100% {
    opacity: 0;
    transform: scale(1.4);
    filter: hue-rotate(90deg) blur(25px);
  }
}

.burn {
  animation: burn 1s ease-in-out forwards;
}

/* ✨ 반대로 생겨나는 애니메이션 */
@keyframes ignite {
  0% {
    opacity: 0;
    transform: scale(1.4);
    filter: hue-rotate(90deg) blur(25px);
  }
  50% {
    opacity: 0.6;
    transform: scale(0.9);
    filter: hue-rotate(0deg) blur(20px);
  }
  100% {
    opacity: 1;
    transform: scale(1);
    filter: none;
  }
}

.ignite {
  animation: ignite 1s ease-in-out forwards;
}
</style>
