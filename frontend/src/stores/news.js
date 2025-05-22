import { defineStore } from 'pinia'
import axios from 'axios'

export const useNewsStore = defineStore('news', {
  state: () => ({
    list: [],
    page: 0,
  }),
  actions: {
    /**
     * 최신 뉴스 목록을 가져옵니다.
     * @param {{ limit: number, offset?: number }} options
     */
    async fetchLatest(options = { limit: 20, offset: 0 }) {
      try {
        const response = await axios.get('https://api.ssafy.blog/api/v1/news/latest', {
          params: {
            limit: options.limit,
            offset: options.offset || 0,
          },
        })
        // 페이지별로 가져온 뉴스를 상태에 설정
        this.list = response.data.data.news
        // console.log(`뉴스 목록 (page ${options.offset / options.limit}):`, this.list)
      } catch (error) {
        console.error('뉴스 불러오기 실패:', error)
      }
    },

    /**
     * 다음 페이지의 뉴스를 가져옵니다.
     * @param {{ limit: number }} options
     */
    async fetchNext(options = { limit: 20 }) {
      this.page += 1
      await this.fetchLatest({ limit: options.limit, offset: this.page * options.limit })
    },

    /**
     * 초기 페이지로 리셋하고 첫 페이지 뉴스 로드
     * @param {{ limit: number }} options
     */
    async init(options = { limit: 20 }) {
      this.page = 0
      await this.fetchLatest({ limit: options.limit, offset: 0 })
    },
  },
})
