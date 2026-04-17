import { defineStore } from 'pinia'
import { ref } from 'vue'
import { categoriesApi } from '@/api/categories'

export const useCategoriesStore = defineStore('categories', () => {
  const categories = ref([])

  async function fetchAll() {
    if (categories.value.length > 0) return
    const res = await categoriesApi.findAll()
    categories.value = res.data.data
  }

  return { categories, fetchAll }
})
