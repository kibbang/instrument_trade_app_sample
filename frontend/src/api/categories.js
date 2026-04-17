import api from './index'

export const categoriesApi = {
  findAll: () => api.get('/categories'),
}
