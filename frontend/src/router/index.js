import { createRouter, createWebHistory } from 'vue-router'
import LoginForm from '../components/LoginForm.vue'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [{
      path: "/",
      name: "home",
      component: () => import("../App.vue"),
    },
    {
      path: "/login",
      name: "login",
      component: LoginForm,
    },
  ],
})

export default router
