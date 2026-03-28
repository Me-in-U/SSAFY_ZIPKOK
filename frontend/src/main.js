import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './assets/tailwind.css'
import './assets/global.css'
import axios from 'axios'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

const localHostPatterns = [
  /^localhost$/,
  /^127(?:\.\d{1,3}){3}$/,
  /^192\.168\.\d{1,3}\.\d{1,3}$/,
  /^10\.\d{1,3}\.\d{1,3}\.\d{1,3}$/,
  /^172\.(1[6-9]|2\d|3[0-1])\.\d{1,3}\.\d{1,3}$/,
]
const currentHost = typeof window !== 'undefined' ? window.location.hostname : ''
const isLocalRuntime = localHostPatterns.some((pattern) => pattern.test(currentHost))
const apiBaseUrl =
  import.meta.env.VITE_API_URL || (isLocalRuntime ? '/api' : 'https://ssafy.ios.kr/api')

// Axios 기본 설정
axios.defaults.baseURL = apiBaseUrl
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
