import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './assets/tailwind.css'
import './assets/global.css'
import axios from 'axios'

// Axios 기본 설정
axios.defaults.baseURL = import.meta.env.VITE_API_URL || '/api'
axios.defaults.headers.common['Content-Type'] = 'application/json'

// 토큰이 있으면 헤더에 추가
const token = localStorage.getItem('token')
if (token) {
  axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
}
const app = createApp(App)
app.use(createPinia())
app.use(router)
app.mount('#app')
