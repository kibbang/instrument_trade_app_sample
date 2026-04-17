<template>
  <RouterLink :to="`/instruments/${instrument.id}`" class="card">
    <div class="card-img">
      <img v-if="instrument.thumbnailUrl" :src="instrument.thumbnailUrl" :alt="instrument.title" />
      <div v-else class="no-img">🎵</div>
      <span class="status-badge" :class="instrument.status.toLowerCase()">{{ statusLabel }}</span>
    </div>
    <div class="card-body">
      <p class="category">{{ instrument.categoryName }}</p>
      <h3 class="title">{{ instrument.title }}</h3>
      <p class="price">{{ instrument.price.toLocaleString() }}원</p>
      <div class="meta">
        <span>{{ instrument.region }}</span>
        <span>조회 {{ instrument.viewCount }}</span>
      </div>
    </div>
  </RouterLink>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  instrument: { type: Object, required: true }
})

const statusLabel = computed(() => ({
  SELLING: '판매중', RESERVED: '예약중', SOLD: '판매완료'
}[props.instrument.status]))
</script>

<style scoped>
.card {
  display: block;
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  transition: transform 0.15s;
}
.card:hover { transform: translateY(-2px); }
.card-img {
  position: relative;
  aspect-ratio: 4/3;
  background: #f0f0f0;
  overflow: hidden;
}
.card-img img { width: 100%; height: 100%; object-fit: cover; }
.no-img {
  width: 100%; height: 100%;
  display: flex; align-items: center; justify-content: center;
  font-size: 2.5rem;
}
.status-badge {
  position: absolute; top: 8px; right: 8px;
  padding: 3px 8px; border-radius: 4px;
  font-size: 0.75rem; font-weight: 600;
}
.status-badge.selling { background: #e8f5e9; color: #2e7d32; }
.status-badge.reserved { background: #fff3e0; color: #e65100; }
.status-badge.sold { background: #eeeeee; color: #757575; }
.card-body { padding: 12px; }
.category { font-size: 0.78rem; color: #888; margin-bottom: 4px; }
.title { font-size: 0.95rem; font-weight: 600; margin-bottom: 6px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis; }
.price { font-size: 1rem; font-weight: 700; color: #1a73e8; margin-bottom: 6px; }
.meta { display: flex; justify-content: space-between; font-size: 0.78rem; color: #aaa; }
</style>
