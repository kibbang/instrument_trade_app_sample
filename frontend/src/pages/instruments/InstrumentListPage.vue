<template>
  <div>
    <InstrumentFilter @search="handleSearch" />
    <div class="result-info">총 {{ totalElements }}개의 악기</div>
    <div v-if="loading" class="loading">불러오는 중...</div>
    <div v-else-if="instruments.length === 0" class="empty">검색 결과가 없습니다.</div>
    <div v-else class="grid">
      <InstrumentCard v-for="item in instruments" :key="item.id" :instrument="item" />
    </div>
    <BasePagination :current-page="currentPage" :total-pages="totalPages" @change="goToPage" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { instrumentsApi } from '@/api/instruments'
import InstrumentCard from '@/components/instrument/InstrumentCard.vue'
import InstrumentFilter from '@/components/instrument/InstrumentFilter.vue'
import BasePagination from '@/components/common/BasePagination.vue'

const instruments = ref([])
const loading = ref(false)
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const condition = reactive({ status: 'SELLING' })

async function fetchList(page = 0) {
  loading.value = true
  try {
    const params = { ...condition, page, size: 20 }
    Object.keys(params).forEach(k => (params[k] === '' || params[k] == null) && delete params[k])
    const res = await instrumentsApi.search(params)
    const data = res.data.data
    instruments.value = data.content
    currentPage.value = data.number
    totalPages.value = data.totalPages
    totalElements.value = data.totalElements
  } finally {
    loading.value = false
  }
}

function handleSearch(filter) {
  Object.assign(condition, filter)
  fetchList(0)
}

function goToPage(page) {
  fetchList(page)
}

onMounted(() => fetchList())
</script>

<style scoped>
.result-info { font-size: 0.9rem; color: #888; margin-bottom: 12px; }
.grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 16px; }
.loading, .empty { text-align: center; padding: 60px; color: #888; font-size: 1rem; }
</style>
