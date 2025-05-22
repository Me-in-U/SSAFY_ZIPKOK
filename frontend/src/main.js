import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './assets/tailwind.css'
import './assets/global.css'
import axios from 'axios'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

// Axios 기본 설정
axios.defaults.baseURL = import.meta.env.VITE_API_URL || '/api'
axios.defaults.headers.common['Content-Type'] = 'application/json'

// 토큰이 있으면 헤더에 추가
const token = localStorage.getItem('jwtToken')
if (token) {
  axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
}
const app = createApp(App)
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
app.use(pinia)
app.use(router)
app.mount('#app')
