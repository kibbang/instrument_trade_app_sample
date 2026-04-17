<template>
  <div>
    <h1>내 판매 목록</h1>
    <div v-if="loading" class="loading">불러오는 중...</div>
    <div v-else-if="instruments.length === 0" class="empty">등록한 악기가 없습니다.</div>
    <div v-else class="grid">
      <InstrumentCard v-for="item in instruments" :key="item.id" :instrument="item" />
    </div>
    <BasePagination :current-page="page" :total-pages="totalPages" @change="fetchList" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { usersApi } from '@/api/users'
import InstrumentCard from '@/components/instrument/InstrumentCard.vue'
import BasePagination from '@/components/common/BasePagination.vue'

const instruments = ref([])
const loading = ref(false)
const page = ref(0)
const totalPages = ref(0)

async function fetchList(p = 0) {
  loading.value = true
  try {
    const res = await usersApi.getMyInstruments({ page: p, size: 20 })
    const data = res.data.data
    instruments.value = data.content
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
.grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 16px; }
.loading, .empty { text-align: center; padding: 60px; color: #888; }
</style>
