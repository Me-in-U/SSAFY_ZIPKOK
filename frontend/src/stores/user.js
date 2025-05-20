// src/stores/user.js
import { defineStore } from 'pinia'
import axios from 'axios'

export const useUserStore = defineStore('user', {
  state: () => ({
    profile: null,
    token: localStorage.getItem('jwtToken') || '',
    favoriteSeqs: [],
  }),
  getters: {
    isLogged: (s) => !!s.token,
    hasFavorites: (s) => s.favoriteSeqs.length > 0,
  },
  actions: {
    setUser(data) {
      this.profile = data.user
      this.token = data.token
      localStorage.setItem('jwtToken', data.token)
    },
    clearUser() {
      this.profile = null
      this.token = ''
      this.favoriteSeqs = []
      localStorage.removeItem('jwtToken')
    },
    async fetchUser() {
      if (!this.token) return
      try {
        const { data } = await axios.get('https://api.ssafy.blog/api/v1/members/me', {
          headers: { Authorization: `Bearer ${this.token}` },
        })
        console.log('[유저 데이터 패치완료]:', data)
        this.profile = data.user
      } catch {
        console.log('[토큰 만료 또는 유효하지 않음]')
        this.clearUser()
      }
    },
    async loadFavorites() {
      // 즐겨찾기 불러오기 액션
      if (!this.profile) return
      const { mno } = this.profile
      const { data } = await axios.get(`http://localhost:8080/api/v1/members/${mno}/favorites`, {
        headers: { Authorization: `Bearer ${this.token}` },
      })
      this.favoriteSeqs = data.data.result.map((item) => item.aptSeq)
      console.log('[즐겨찾기 로딩 완료]:', this.favoriteSeqs)
    },
  },
  persist: {
    key: 'zipkok-user',
    storage: window.localStorage,
    paths: ['token'],
  },
})
