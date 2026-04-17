import api from './index'

export const usersApi = {
  getMe: () => api.get('/users/me'),
  updateProfile: (data) => api.put('/users/me', data),
  changePassword: (data) => api.put('/users/me/password', data),
  getProfile: (userId) => api.get(`/users/${userId}/profile`),
  getMyInstruments: (params) => api.get('/users/me/instruments', { params }),
  getMyWishlists: (params) => api.get('/users/me/wishlists', { params }),
}
