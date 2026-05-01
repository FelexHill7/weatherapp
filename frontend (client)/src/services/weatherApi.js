import axios from 'axios'

const api = axios.create({ baseURL: '/api' })

api.interceptors.response.use(
  res => res,
  err => {
    const message =
      err.response?.data?.message ||
      err.response?.data?.error ||
      err.message ||
      'An unexpected error occurred'
    return Promise.reject(new Error(message))
  }
)

export const weatherApi = {
  getCurrentWeather: (location) =>
    api.get('/weather/current', { params: { location } }).then(r => r.data),

  getForecast: (location) =>
    api.get('/weather/forecast', { params: { location } }).then(r => r.data),

  createRecord: (location, startDate, endDate) =>
    api.post('/weather/records', { location, startDate, endDate }).then(r => r.data),

  getAllRecords: () =>
    api.get('/weather/records').then(r => r.data),

  getRecord: (id) =>
    api.get(`/weather/records/${id}`).then(r => r.data),

  updateRecord: (id, location, startDate, endDate) =>
    api.put(`/weather/records/${id}`, { location, startDate, endDate }).then(r => r.data),

  deleteRecord: (id) =>
    api.delete(`/weather/records/${id}`),

  getVideos: (location) =>
    api.get('/weather/videos', { params: { location } }).then(r => r.data),

  getMapInfo: (location) =>
    api.get('/weather/map', { params: { location } }).then(r => r.data),

  exportData: (format) =>
    api.get('/export', { params: { format }, responseType: 'blob' }).then(r => r),
}
