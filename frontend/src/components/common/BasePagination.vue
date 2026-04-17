<template>
  <div v-if="totalPages > 1" class="pagination">
    <button :disabled="currentPage === 0" @click="emit('change', currentPage - 1)">이전</button>
    <button
      v-for="p in pages" :key="p"
      :class="{ active: p === currentPage }"
      @click="emit('change', p)"
    >{{ p + 1 }}</button>
    <button :disabled="currentPage === totalPages - 1" @click="emit('change', currentPage + 1)">다음</button>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  currentPage: { type: Number, default: 0 },
  totalPages: { type: Number, default: 0 }
})
const emit = defineEmits(['change'])

const pages = computed(() => {
  const start = Math.max(0, props.currentPage - 2)
  const end = Math.min(props.totalPages - 1, start + 4)
  return Array.from({ length: end - start + 1 }, (_, i) => start + i)
})
</script>

<style scoped>
.pagination {
  display: flex; justify-content: center; gap: 6px; margin-top: 24px;
}
button {
  padding: 6px 12px; border: 1px solid #ddd; border-radius: 4px;
  background: #fff; color: #555; font-size: 0.9rem;
}
button:disabled { opacity: 0.4; cursor: not-allowed; }
button.active { background: #1a73e8; color: #fff; border-color: #1a73e8; }
</style>
