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
async function sendMessage() {
  const userText = inputMessage.value.trim()
  if (!userText) return

  messages.value.push({ content: userText, sender: 'user' })
  inputMessage.value = ''
  nextTick(scrollToBottom)
  isTyping.value = true

  try {
    const payload = { message: userText }

    console.log('[Request Payload]', payload)
    //  const res = await fetch('https://api.ssafy.blog/ai/member', {
    const res = await fetch('http://localhost:8080/ai/house', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ message: userText }),
    })

    console.log('[Response Status]', res.status)

    if (!res.ok) {
      throw new Error(`서버 응답 실패: ${res.status} ${res.statusText}`)
    }

    const result = await res.json()
    console.log('[Parsed Result]', result)

    const botReply = result.data.message || '응답이 없습니다.'
    messages.value.push({ content: botReply, sender: 'bot' })
    // ✅ 지도 데이터가 있을 경우에만 마커 표시
    if (Array.isArray(result.data.houses)) {
      result.data.houses.forEach((apt) => {
        const lat = parseFloat(apt.latitude)
        const lng = parseFloat(apt.longitude)
        const name = apt.aptNm
        console.log('[Map Data]', lat, lng, name)
        // if (!isNaN(lat) && !isNaN(lng)) {
        //   const marker = new kakao.maps.Marker({
        //     map: window.map, // ❗ 반드시 전역 map 객체 존재해야 함
        //     position: new kakao.maps.LatLng(lat, lng),
        //     title: name,
        //   })

        //   // 💡 마커 클릭 시 간단한 인포윈도우 열기
        //   const infowindow = new kakao.maps.InfoWindow({
        //     content: `<div style="padding:5px;font-size:12px;">${name}</div>`,
        //   })
        //   kakao.maps.event.addListener(marker, 'click', function () {
        //     infowindow.open(window.map, marker)
        //   })
        // }
      })

      // 지도 중심을 첫 번째 마커 위치로 이동
      // const first = result.data.houses[0]
      // if (first) {
      //   window.map.setCenter(new kakao.maps.LatLng(first.latitude, first.longitude))
      // }
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
