import api from './index'

export const tradesApi = {
  createRequest: (instrumentId, data) => api.post(`/instruments/${instrumentId}/trade-requests`, data),
  accept: (id) => api.patch(`/trade-requests/${id}/accept`),
  reject: (id) => api.patch(`/trade-requests/${id}/reject`),
  complete: (id) => api.patch(`/trade-requests/${id}/complete`),
  cancel: (id) => api.patch(`/trade-requests/${id}/cancel`),
  getSentRequests: (params) => api.get('/users/me/trade-requests/sent', { params }),
  getReceivedRequests: (params) => api.get('/users/me/trade-requests/received', { params }),
}
