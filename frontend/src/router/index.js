// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import AppLayout from '@/views/AppLayout.vue'
import HomeContent from '@/views/HomeContent.vue'
import LoginForm from '@/components/LoginForm.vue'
import RegisterForm from '@/components/RegisterForm.vue'
import RecommendedPropertiesModal from '@/components/RecommendedPropertiesModal.vue'
import MyPage from '@/views/MyPage.vue'

export default createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: AppLayout,
      children: [
        { path: '', component: HomeContent },
        {
          path: 'login',
          components: { default: HomeContent, modal: LoginForm },
        },
        {
          path: 'register',
          components: { default: HomeContent, modal: RegisterForm },
        },
        {
          path: 'recommended',
          components: { default: HomeContent, modal: RecommendedPropertiesModal },
        },
      ],
    },
    {
      path: '/mypage',
      component: MyPage,
      children: [
        {
          path: '',
          component: MyPage,
        }
      ]
    }
  ],
})
