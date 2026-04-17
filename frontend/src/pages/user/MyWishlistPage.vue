<template>
  <div>
    <h1>찜 목록</h1>
    <div v-if="loading" class="loading">불러오는 중...</div>
    <div v-else-if="items.length === 0" class="empty">찜한 악기가 없습니다.</div>
    <div v-else class="list">
      <RouterLink
        v-for="item in items" :key="item.instrumentId"
        :to="`/instruments/${item.instrumentId}`"
        class="wish-item"
      >
        <img v-if="item.thumbnailUrl" :src="item.thumbnailUrl" :alt="item.title" />
        <div v-else class="no-img">🎵</div>
        <div class="wish-info">
          <h3>{{ item.title }}</h3>
          <p class="price">{{ item.price.toLocaleString() }}원</p>
          <p class="region">📍 {{ item.region }}</p>
        </div>
      </RouterLink>
    </div>
    <BasePagination :current-page="page" :total-pages="totalPages" @change="fetchList" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { usersApi } from '@/api/users'
import BasePagination from '@/components/common/BasePagination.vue'

const items = ref([])
const loading = ref(false)
const page = ref(0)
const totalPages = ref(0)

async function fetchList(p = 0) {
  loading.value = true
  try {
    const res = await usersApi.getMyWishlists({ page: p, size: 20 })
    const data = res.data.data
    items.value = data.content
    page.value = data.number
    totalPages.value = data.totalPages
  } finally {
    loading.value = false
  }
}

onMounted(() => fetchList())
</script>

<style scoped>
h1 { font-size: 1.4rem; margin-bottom: 20px; }
.list { display: flex; flex-direction: column; gap: 12px; }
.wish-item { display: flex; gap: 16px; background: #fff; border-radius: 10px; padding: 16px; box-shadow: 0 1px 4px rgba(0,0,0,0.08); align-items: center; color: #333; }
.wish-item:hover { box-shadow: 0 3px 10px rgba(0,0,0,0.12); }
.wish-item img, .no-img { width: 80px; height: 80px; border-radius: 8px; object-fit: cover; flex-shrink: 0; }
.no-img { background: #f0f0f0; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; }
.wish-info h3 { font-size: 1rem; font-weight: 600; margin-bottom: 4px; }
.price { color: #1a73e8; font-weight: 700; margin-bottom: 4px; }
.region { font-size: 0.85rem; color: #888; }
.loading, .empty { text-align: center; padding: 60px; color: #888; }
</style>
