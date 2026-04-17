<template>
  <div class="auth-wrap">
    <div class="auth-box">
      <h1>로그인</h1>
      <form @submit.prevent="handleLogin">
        <div class="field">
          <label>이메일</label>
          <input v-model="form.email" type="email" placeholder="이메일 입력" required />
        </div>
        <div class="field">
          <label>비밀번호</label>
          <input v-model="form.password" type="password" placeholder="비밀번호 입력" required />
        </div>
        <p v-if="error" class="error">{{ error }}</p>
        <button type="submit" :disabled="loading">{{ loading ? '로그인 중...' : '로그인' }}</button>
      </form>
      <p class="link">계정이 없으신가요? <RouterLink to="/signup">회원가입</RouterLink></p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter, useRoute } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()

const form = reactive({ email: '', password: '' })
const error = ref('')
const loading = ref(false)

async function handleLogin() {
  error.value = ''
  loading.value = true
  try {
    await authStore.login(form.email, form.password)
    router.push(route.query.redirect || '/')
  } catch (e) {
    error.value = e.response?.data?.message || '로그인에 실패했습니다.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-wrap { display: flex; justify-content: center; padding: 40px 16px; }
.auth-box {
  background: #fff; border-radius: 12px; padding: 36px;
  width: 100%; max-width: 420px; box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}
h1 { font-size: 1.5rem; margin-bottom: 24px; text-align: center; }
.field { margin-bottom: 16px; }
label { display: block; font-size: 0.9rem; margin-bottom: 6px; color: #555; }
input {
  width: 100%; padding: 10px 12px; border: 1px solid #ddd;
  border-radius: 6px; font-size: 0.95rem; outline: none;
}
input:focus { border-color: #1a73e8; }
button {
  width: 100%; padding: 12px; background: #1a73e8; color: #fff;
  border: none; border-radius: 8px; font-size: 1rem; margin-top: 8px;
}
button:disabled { opacity: 0.6; }
.error { color: #d32f2f; font-size: 0.88rem; margin-bottom: 8px; }
.link { text-align: center; margin-top: 16px; font-size: 0.9rem; color: #666; }
.link a { color: #1a73e8; }
</style>
