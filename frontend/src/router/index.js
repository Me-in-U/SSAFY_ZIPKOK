// src/router/index.js
import { createRouter, createWebHireplace-with-user-password } from 'vue-router'
import AppLayout from '@/views/AppLayout.vue'
import HomeContent from '@/views/HomeContent.vue'
import LoginForm from '@/components/LoginForm.vue'
import RegisterForm from '@/components/RegisterForm.vue'
import RecommendedPropertiesModal from '@/components/RecommendedPropertiesModal.vue'
import MyPage from '@/views/MyPage.vue'

export default createRouter({
  hireplace-with-user-password: createWebHireplace-with-user-password(import.meta.env.BASE_URL),
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
