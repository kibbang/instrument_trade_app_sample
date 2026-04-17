<template>
  <div class="profile-wrap">
    <h1>마이페이지</h1>
    <div class="card">
      <div class="avatar">{{ authStore.user?.nickname?.charAt(0) }}</div>
      <div class="user-info">
        <p class="nickname">{{ authStore.user?.nickname }}</p>
        <p class="email">{{ authStore.user?.email }}</p>
        <p v-if="authStore.user?.region" class="region">📍 {{ authStore.user?.region }}</p>
      </div>
    </div>
    <div class="menu-grid">
      <RouterLink to="/me/instruments" class="menu-item">
        <span class="icon">📋</span>
        <span>내 판매 목록</span>
      </RouterLink>
      <RouterLink to="/me/wishlist" class="menu-item">
        <span class="icon">♥</span>
        <span>찜 목록</span>
      </RouterLink>
      <RouterLink to="/me/trades/received" class="menu-item">
        <span class="icon">🤝</span>
        <span>거래 관리</span>
      </RouterLink>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth'
const authStore = useAuthStore()
</script>

<style scoped>
.profile-wrap { max-width: 600px; margin: 0 auto; }
h1 { font-size: 1.4rem; margin-bottom: 20px; }
.card { background: #fff; border-radius: 12px; padding: 24px; display: flex; align-items: center; gap: 20px; box-shadow: 0 1px 4px rgba(0,0,0,0.08); margin-bottom: 20px; }
.avatar { width: 60px; height: 60px; border-radius: 50%; background: #1a73e8; color: #fff; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; font-weight: 700; flex-shrink: 0; }
.nickname { font-size: 1.1rem; font-weight: 700; }
.email { color: #888; font-size: 0.9rem; margin-top: 4px; }
.region { color: #777; font-size: 0.9rem; margin-top: 4px; }
.menu-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
.menu-item { background: #fff; border-radius: 12px; padding: 24px; display: flex; flex-direction: column; align-items: center; gap: 10px; box-shadow: 0 1px 4px rgba(0,0,0,0.08); color: #333; transition: box-shadow 0.15s; }
.menu-item:hover { box-shadow: 0 3px 10px rgba(0,0,0,0.12); }
.icon { font-size: 1.8rem; }
</style>
