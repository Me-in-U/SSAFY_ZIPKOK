<template>
  <div v-if="showCookieBanner" class="fixed bottom-4 right-4 bg-yellow-100 p-4 rounded shadow">
    <p>서비스 이용을 위해 브라우저 설정에서 <strong>타사 쿠키 허용</strong>을 켜주세요.</p>
    <button @click="openHelp" class="ml-2 px-2 py-1 bg-blue-600 text-white rounded">
      방법 보기
    </button>
    <button @click="showCookieBanner = false" class="ml-2 px-2 py-1 text-gray-600">닫기</button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const showCookieBanner = ref(false)

// 1. 3자 쿠키 차단 여부 감지
function isThirdPartyCookieBlocked() {
  try {
    document.cookie = 'test3pc=1; SameSite=None; Secure'
    return document.cookie.indexOf('test3pc=') === -1
  } catch {
    return true
  }
}

onMounted(() => {
  if (isThirdPartyCookieBlocked()) {
    showCookieBanner.value = true
  }
})

function openHelp() {
  window.open('https://support.google.com/chrome/answer/95647', '_blank')
}
</script>
