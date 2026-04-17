import api from './index'

export const instrumentsApi = {
  search: (params) => api.get('/instruments', { params }),
  create: (data) => api.post('/instruments', data),
  getDetail: (id) => api.get(`/instruments/${id}`),
  update: (id, data) => api.put(`/instruments/${id}`, data),
  delete: (id) => api.delete(`/instruments/${id}`),
  updateStatus: (id, status) => api.patch(`/instruments/${id}/status`, { status }),
  uploadImages: (id, formData) => api.post(`/instruments/${id}/images`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  deleteImage: (id, imageId) => api.delete(`/instruments/${id}/images/${imageId}`),
  addWishlist: (id) => api.post(`/instruments/${id}/wishlist`),
  removeWishlist: (id) => api.delete(`/instruments/${id}/wishlist`),
  getMyStatus: (id) => api.get(`/instruments/${id}/my-status`),
}
