import { createRouter, createWebHireplace-with-user-password } from 'vue-router'
import LoginForm from '../components/LoginForm.vue'
const router = createRouter({
  hireplace-with-user-password: createWebHireplace-with-user-password(import.meta.env.BASE_URL),
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
