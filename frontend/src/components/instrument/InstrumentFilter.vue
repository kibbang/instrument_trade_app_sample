<template>
  <div class="filter">
    <input v-model="local.keyword" type="text" placeholder="악기 검색..." class="search-input" @keyup.enter="emit('search', local)" />
    <select v-model="local.categoryId">
      <option value="">전체 카테고리</option>
      <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
    </select>
    <select v-model="local.status">
      <option value="">전체 상태</option>
      <option value="SELLING">판매중</option>
      <option value="RESERVED">예약중</option>
      <option value="SOLD">판매완료</option>
    </select>
    <input v-model.number="local.minPrice" type="number" placeholder="최소 가격" class="price-input" />
    <input v-model.number="local.maxPrice" type="number" placeholder="최대 가격" class="price-input" />
    <input v-model="local.region" type="text" placeholder="지역" class="region-input" />
    <button class="btn-search" @click="emit('search', local)">검색</button>
    <button class="btn-reset" @click="reset">초기화</button>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useCategoriesStore } from '@/stores/categories'
import { storeToRefs } from 'pinia'

const props = defineProps({ modelValue: Object })
const emit = defineEmits(['search'])

const categoriesStore = useCategoriesStore()
const { categories } = storeToRefs(categoriesStore)
categoriesStore.fetchAll()

const local = reactive({ keyword: '', categoryId: '', status: 'SELLING', minPrice: null, maxPrice: null, region: '' })

function reset() {
  Object.assign(local, { keyword: '', categoryId: '', status: 'SELLING', minPrice: null, maxPrice: null, region: '' })
  emit('search', local)
}
</script>

<style scoped>
.filter {
  display: flex; flex-wrap: wrap; gap: 8px; align-items: center;
  background: #fff; padding: 16px; border-radius: 10px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08); margin-bottom: 20px;
}
.search-input { flex: 2; min-width: 180px; }
.price-input { width: 120px; }
.region-input { width: 120px; }
input, select {
  padding: 8px 10px; border: 1px solid #ddd; border-radius: 6px;
  font-size: 0.9rem; outline: none;
}
input:focus, select:focus { border-color: #1a73e8; }
.btn-search {
  padding: 8px 18px; background: #1a73e8; color: #fff;
  border: none; border-radius: 6px; font-size: 0.9rem;
}
.btn-reset {
  padding: 8px 14px; background: #fff; border: 1px solid #ddd;
  border-radius: 6px; font-size: 0.9rem; color: #666;
}
</style>
