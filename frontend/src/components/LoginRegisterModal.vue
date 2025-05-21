<template>
  <teleport to="body">
    <router-view name="modal" v-slot="{ Component }">
      <transition name="fade">
        <div
          v-if="Component"
          class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
        >
          <component
            :is="Component"
            @login-success="emit('login-success')"
            @register-success="emit('register-success')"
            @close="emit('close')"
          />
        </div>
      </transition>
    </router-view>
  </teleport>
</template>

<script setup>
const emit = defineEmits(['login-success', 'register-success', 'close'])
</script>

<style>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
