import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { useAuthStore } from '@/stores/auth'

const app = createApp(App)
const pinia = createPinia()
app.use(pinia)
app.use(router)

// 페이지 새로고침 시 localStorage의 refreshToken으로 로그인 상태 복원
const authStore = useAuthStore()
authStore.initAuth().finally(() => {
  app.mount('#app')
})
