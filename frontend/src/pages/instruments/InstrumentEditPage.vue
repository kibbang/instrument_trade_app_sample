<template>
  <div v-if="loaded" class="form-wrap">
    <h1>악기 수정</h1>
    <form @submit.prevent="handleSubmit">
      <div class="field">
        <label>카테고리 *</label>
        <select v-model="form.categoryId" required>
          <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
        </select>
      </div>
      <div class="field">
        <label>제목 *</label>
        <input v-model="form.title" type="text" required />
      </div>
      <div class="field">
        <label>가격 (원) *</label>
        <input v-model.number="form.price" type="number" min="0" required />
      </div>
      <div class="field">
        <label>지역</label>
        <input v-model="form.region" type="text" />
      </div>
      <div class="field">
        <label>상품 설명</label>
        <textarea v-model="form.description" rows="6" />
      </div>
      <p v-if="error" class="error">{{ error }}</p>
      <div class="btn-row">
        <RouterLink :to="`/instruments/${route.params.id}`" class="btn-cancel">취소</RouterLink>
        <button type="submit" :disabled="loading">{{ loading ? '저장 중...' : '저장하기' }}</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { instrumentsApi } from '@/api/instruments'
import { useCategoriesStore } from '@/stores/categories'
import { storeToRefs } from 'pinia'

const route = useRoute()
const router = useRouter()
const categoriesStore = useCategoriesStore()
const { categories } = storeToRefs(categoriesStore)
categoriesStore.fetchAll()

const form = reactive({ categoryId: null, title: '', price: null, region: '', description: '' })
const loaded = ref(false)
const error = ref('')
const loading = ref(false)

onMounted(async () => {
  const res = await instrumentsApi.getDetail(route.params.id)
  const d = res.data.data
  Object.assign(form, { categoryId: d.categoryId, title: d.title, price: d.price, region: d.region, description: d.description })
  loaded.value = true
})

async function handleSubmit() {
  error.value = ''
  loading.value = true
  try {
    await instrumentsApi.update(route.params.id, form)
    router.push(`/instruments/${route.params.id}`)
  } catch (e) {
    error.value = e.response?.data?.message || '수정에 실패했습니다.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.form-wrap { max-width: 700px; margin: 0 auto; background: #fff; border-radius: 12px; padding: 32px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
h1 { font-size: 1.4rem; margin-bottom: 24px; }
.field { margin-bottom: 18px; }
label { display: block; font-size: 0.9rem; color: #555; margin-bottom: 6px; }
input, select, textarea { width: 100%; padding: 10px 12px; border: 1px solid #ddd; border-radius: 6px; font-size: 0.95rem; outline: none; }
input:focus, select:focus, textarea:focus { border-color: #1a73e8; }
textarea { resize: vertical; font-family: inherit; }
.error { color: #d32f2f; font-size: 0.88rem; margin-bottom: 12px; }
.btn-row { display: flex; gap: 12px; justify-content: flex-end; }
.btn-row button { padding: 12px 28px; background: #1a73e8; color: #fff; border: none; border-radius: 8px; font-size: 1rem; }
.btn-row button:disabled { opacity: 0.6; }
.btn-cancel { padding: 12px 20px; border: 1px solid #ddd; border-radius: 8px; color: #666; }
</style>
