<template>
  <div v-if="profile" class="profile-wrap">
    <div class="card">
      <div class="avatar">{{ profile.nickname.charAt(0) }}</div>
      <div>
        <p class="nickname">{{ profile.nickname }}</p>
        <p v-if="profile.region" class="region">📍 {{ profile.region }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { usersApi } from '@/api/users'

const route = useRoute()
const profile = ref(null)

onMounted(async () => {
  const res = await usersApi.getProfile(route.params.userId)
  profile.value = res.data.data
})
</script>

<style scoped>
.profile-wrap { max-width: 500px; margin: 0 auto; }
.card { background: #fff; border-radius: 12px; padding: 28px; display: flex; align-items: center; gap: 20px; box-shadow: 0 1px 4px rgba(0,0,0,0.08); }
.avatar { width: 60px; height: 60px; border-radius: 50%; background: #1a73e8; color: #fff; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; font-weight: 700; }
.nickname { font-size: 1.1rem; font-weight: 700; }
.region { color: #777; font-size: 0.9rem; margin-top: 4px; }
</style>
