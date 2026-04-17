<template>
  <div>
    <section class="hero">
      <h1>중고 악기를 사고팔다</h1>
      <p>기타, 피아노, 드럼 등 다양한 악기를 합리적인 가격에 거래하세요.</p>
      <RouterLink to="/instruments" class="btn-primary">악기 둘러보기</RouterLink>
    </section>

    <section class="recent">
      <h2>최근 등록 악기</h2>
      <div v-if="loading" class="loading">불러오는 중...</div>
      <div v-else class="grid">
        <InstrumentCard v-for="item in instruments" :key="item.id" :instrument="item" />
      </div>
      <div class="more">
        <RouterLink to="/instruments" class="btn-outline">더 보기</RouterLink>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { instrumentsApi } from '@/api/instruments'
import InstrumentCard from '@/components/instrument/InstrumentCard.vue'

const instruments = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await instrumentsApi.search({ status: 'SELLING', size: 8 })
    instruments.value = res.data.data.content
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.hero {
  background: linear-gradient(135deg, #1a73e8, #0d47a1);
  color: #fff; text-align: center;
  padding: 60px 16px; border-radius: 12px; margin-bottom: 40px;
}
.hero h1 { font-size: 2rem; margin-bottom: 12px; }
.hero p { font-size: 1.1rem; margin-bottom: 24px; opacity: 0.9; }
.btn-primary {
  display: inline-block; background: #fff; color: #1a73e8;
  padding: 12px 28px; border-radius: 8px; font-weight: 700; font-size: 1rem;
}
.recent h2 { font-size: 1.3rem; margin-bottom: 16px; }
.grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 16px; }
.loading { text-align: center; padding: 40px; color: #888; }
.more { text-align: center; margin-top: 24px; }
.btn-outline {
  display: inline-block; border: 2px solid #1a73e8; color: #1a73e8;
  padding: 10px 24px; border-radius: 8px; font-weight: 600;
}
</style>
