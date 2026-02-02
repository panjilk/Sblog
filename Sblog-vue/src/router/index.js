import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path:"/index", component: ()=> import("@/views/index.vue"
  )},
  {
    path: "/login",
    component: () => import("@/views/login.vue")
  },
  {
    path: "/register",
    component: () => import("@/views/register.vue")
  },
  {
    path: "/" , redirect:"/index"
  },
  {
    path:"/admin", component: ()=> import("@/views/admin.vue")
  }
],
})

export default router
