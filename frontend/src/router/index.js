import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  { path: '/', component: () => import('@/pages/HomePage.vue') },
  { path: '/login', component: () => import('@/pages/auth/LoginPage.vue'), meta: { guest: true } },
  { path: '/signup', component: () => import('@/pages/auth/SignupPage.vue'), meta: { guest: true } },
  { path: '/instruments', component: () => import('@/pages/instruments/InstrumentListPage.vue') },
  { path: '/instruments/new', component: () => import('@/pages/instruments/InstrumentCreatePage.vue'), meta: { auth: true } },
  { path: '/instruments/:id', component: () => import('@/pages/instruments/InstrumentDetailPage.vue') },
  { path: '/instruments/:id/edit', component: () => import('@/pages/instruments/InstrumentEditPage.vue'), meta: { auth: true } },
  { path: '/me', component: () => import('@/pages/user/MyProfilePage.vue'), meta: { auth: true } },
  { path: '/me/instruments', component: () => import('@/pages/user/MyInstrumentsPage.vue'), meta: { auth: true } },
  { path: '/me/wishlist', component: () => import('@/pages/user/MyWishlistPage.vue'), meta: { auth: true } },
  { path: '/me/trades', redirect: '/me/trades/received' },
  { path: '/me/trades/received', component: () => import('@/pages/user/MyTradesPage.vue'), meta: { auth: true } },
  { path: '/me/trades/sent', component: () => import('@/pages/user/MyTradesPage.vue'), meta: { auth: true } },
  { path: '/users/:userId', component: () => import('@/pages/user/UserProfilePage.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  if (to.meta.auth && !authStore.isLoggedIn) {
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }
  if (to.meta.guest && authStore.isLoggedIn) {
    return next('/')
  }
  next()
})

export default router
