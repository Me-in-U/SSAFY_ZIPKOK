<template>
  <div class="whitespace-pre-wrap leading-relaxed">
    <span
      v-for="(word, idx) in displayedWords"
      :key="idx"
      :class="[
        'inline-block transition-opacity duration-500',
        revealedIndexes.includes(idx) ? 'opacity-100' : 'opacity-0',
      ]"
    >
      {{ word }}
    </span>
    <span
      v-if="isTyping"
      class="ml-1 w-2 h-2 inline-block bg-gray-400 rounded-full animate-bounce align-middle"
    ></span>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'

// emit props
const emit = defineEmits(['done'])
const props = defineProps({
  text: String,
  speed: { type: Number, default: 200 },
})

// refs
const displayedWords = ref([])
const revealedIndexes = ref([])
const isTyping = ref(true)

// onMounted
onMounted(() => {
  const words = props.text.split(' ')
  let index = 0

  const typeNext = async () => {
    if (index < words.length) {
      displayedWords.value.push(words[index] + ' ')
      await nextTick()
      revealedIndexes.value.push(index) // 페이드인 트리거
      index++
      scrollSmoothly()
      setTimeout(typeNext, props.speed)
    } else {
      isTyping.value = false
      emit('done')
    }
  }

  typeNext()
})

function scrollSmoothly() {
  const container =
    document.querySelector('[ref="messagesContainer"]') || document.querySelector('.scroll-smooth')
  if (container) {
    container.scrollTop = container.scrollHeight
  }
}
</script>
