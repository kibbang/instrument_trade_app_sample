import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const accessToken = ref(null)
  const refreshTokenValue = ref(localStorage.getItem('refreshToken'))

  const isLoggedIn = computed(() => !!accessToken.value)

  async function login(email, password) {
    const res = await authApi.login({ email, password })
    const { accessToken: at, refreshToken: rt, user: u } = res.data.data
    accessToken.value = at
    refreshTokenValue.value = rt
    user.value = u
    localStorage.setItem('refreshToken', rt)
  }

  async function signup(data) {
    await authApi.signup(data)
  }

  async function logout() {
    try {
      await authApi.logout()
    } finally {
      clearAuth()
    }
  }

  async function refreshToken() {
    const rt = refreshTokenValue.value
    if (!rt) throw new Error('No refresh token')
    const res = await authApi.refresh(rt)
    const { accessToken: at, refreshToken: newRt, user: u } = res.data.data
    accessToken.value = at
    refreshTokenValue.value = newRt
    user.value = u
    localStorage.setItem('refreshToken', newRt)
  }

  function clearAuth() {
    user.value = null
    accessToken.value = null
    refreshTokenValue.value = null
    localStorage.removeItem('refreshToken')
  }

  async function initAuth() {
    if (refreshTokenValue.value && !accessToken.value) {
      try {
        await refreshToken()
      } catch {
        clearAuth()
      }
    }
  }

  return { user, accessToken, isLoggedIn, login, signup, logout, refreshToken, clearAuth, initAuth }
})
