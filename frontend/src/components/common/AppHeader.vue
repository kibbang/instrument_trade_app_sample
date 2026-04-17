<template>
  <header class="header">
    <div class="header-inner">
      <RouterLink to="/" class="logo">🎸 중고악기거래</RouterLink>
      <nav class="nav">
        <RouterLink to="/instruments">악기 목록</RouterLink>
        <template v-if="authStore.isLoggedIn">
          <RouterLink to="/instruments/new">판매하기</RouterLink>
          <RouterLink to="/me">마이페이지</RouterLink>
          <button class="btn-logout" @click="handleLogout">로그아웃</button>
        </template>
        <template v-else>
          <RouterLink to="/login">로그인</RouterLink>
          <RouterLink to="/signup" class="btn-signup">회원가입</RouterLink>
        </template>
      </nav>
    </div>
  </header>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

async function handleLogout() {
  await authStore.logout()
  router.push('/')
}
</script>

<style scoped>
.header {
  background: #fff;
  border-bottom: 1px solid #e0e0e0;
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 16px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.logo {
  font-size: 1.3rem;
  font-weight: 700;
  color: #333;
}
.nav {
  display: flex;
  align-items: center;
  gap: 20px;
  font-size: 0.95rem;
}
.nav a { color: #555; }
.nav a:hover { color: #1a73e8; }
.btn-signup {
  background: #1a73e8;
  color: #fff !important;
  padding: 6px 14px;
  border-radius: 6px;
}
.btn-logout {
  background: none;
  border: 1px solid #ccc;
  padding: 6px 14px;
  border-radius: 6px;
  font-size: 0.9rem;
  color: #555;
}
.btn-logout:hover { border-color: #999; }
</style>
