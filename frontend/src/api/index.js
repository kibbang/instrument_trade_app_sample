import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

const api = axios.create({
  baseURL: '/api/v1',
  headers: { 'Content-Type': 'application/json' }
})

api.interceptors.request.use((config) => {
  const authStore = useAuthStore()
  if (authStore.accessToken) {
    config.headers.Authorization = `Bearer ${authStore.accessToken}`
  }
  return config
})

api.interceptors.response.use(
  (res) => res,
  async (err) => {
    if (err.response?.status === 401 && !err.config._retry) {
      err.config._retry = true
      try {
        const authStore = useAuthStore()
        await authStore.refreshToken()
        err.config.headers.Authorization = `Bearer ${authStore.accessToken}`
        return api(err.config)
      } catch {
        const authStore = useAuthStore()
        authStore.clearAuth()
        window.location.href = '/login'
      }
    }
    return Promise.reject(err)
  }
)

export default api
