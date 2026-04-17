<template>
  <div class="form-wrap">
    <h1>악기 등록</h1>
    <form @submit.prevent="handleSubmit">
      <div class="field">
        <label>카테고리 *</label>
        <select v-model="form.categoryId" required>
          <option value="">선택하세요</option>
          <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
        </select>
      </div>
      <div class="field">
        <label>제목 *</label>
        <input v-model="form.title" type="text" placeholder="악기명과 상태를 간략히 작성해주세요" required />
      </div>
      <div class="field">
        <label>가격 (원) *</label>
        <input v-model.number="form.price" type="number" min="0" placeholder="0" required />
      </div>
      <div class="field">
        <label>지역</label>
        <input v-model="form.region" type="text" placeholder="예: 서울 강남구" />
      </div>
      <div class="field">
        <label>상품 설명</label>
        <textarea v-model="form.description" rows="6" placeholder="악기 상태, 구매 시기 등을 자세히 적어주세요" />
      </div>
      <div class="field">
        <label>이미지 (최대 10장)</label>
        <input type="file" multiple accept="image/*" @change="handleFiles" />
        <div class="preview-grid">
          <div v-for="(url, i) in previews" :key="i" class="preview-item">
            <img :src="url" />
            <button type="button" @click="removeFile(i)">✕</button>
          </div>
        </div>
      </div>
      <p v-if="error" class="error">{{ error }}</p>
      <div class="btn-row">
        <RouterLink to="/instruments" class="btn-cancel">취소</RouterLink>
        <button type="submit" :disabled="loading">{{ loading ? '등록 중...' : '등록하기' }}</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { instrumentsApi } from '@/api/instruments'
import { useCategoriesStore } from '@/stores/categories'
import { storeToRefs } from 'pinia'

const router = useRouter()
const categoriesStore = useCategoriesStore()
const { categories } = storeToRefs(categoriesStore)
categoriesStore.fetchAll()

const form = reactive({ categoryId: '', title: '', price: null, region: '', description: '' })
const files = ref([])
const previews = ref([])
const error = ref('')
const loading = ref(false)

function handleFiles(e) {
  const selected = Array.from(e.target.files).slice(0, 10 - files.value.length)
  selected.forEach(f => {
    files.value.push(f)
    previews.value.push(URL.createObjectURL(f))
  })
}

function removeFile(index) {
  files.value.splice(index, 1)
  previews.value.splice(index, 1)
}

async function handleSubmit() {
  error.value = ''
  loading.value = true
  try {
    const res = await instrumentsApi.create(form)
    const id = res.data.data.id
    if (files.value.length > 0) {
      const fd = new FormData()
      files.value.forEach(f => fd.append('images', f))
      await instrumentsApi.uploadImages(id, fd)
    }
    router.push(`/instruments/${id}`)
  } catch (e) {
    error.value = e.response?.data?.message || '등록에 실패했습니다.'
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
input, select, textarea {
  width: 100%; padding: 10px 12px; border: 1px solid #ddd;
  border-radius: 6px; font-size: 0.95rem; outline: none;
}
input:focus, select:focus, textarea:focus { border-color: #1a73e8; }
textarea { resize: vertical; font-family: inherit; }
.preview-grid { display: flex; gap: 8px; flex-wrap: wrap; margin-top: 10px; }
.preview-item { position: relative; }
.preview-item img { width: 80px; height: 80px; object-fit: cover; border-radius: 6px; }
.preview-item button { position: absolute; top: -4px; right: -4px; background: #f44336; color: #fff; border: none; border-radius: 50%; width: 20px; height: 20px; font-size: 0.7rem; }
.error { color: #d32f2f; font-size: 0.88rem; margin-bottom: 12px; }
.btn-row { display: flex; gap: 12px; justify-content: flex-end; }
.btn-row button { padding: 12px 28px; background: #1a73e8; color: #fff; border: none; border-radius: 8px; font-size: 1rem; }
.btn-row button:disabled { opacity: 0.6; }
.btn-cancel { padding: 12px 20px; border: 1px solid #ddd; border-radius: 8px; color: #666; }
</style>
