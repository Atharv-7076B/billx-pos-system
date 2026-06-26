import axios from 'axios'
import toast from 'react-hot-toast'

const api = axios.create({
  baseURL: '/',
  headers: { 'Content-Type': 'application/json' },
})

// Attach JWT
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('billx_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// Handle errors globally
api.interceptors.response.use(
  (res) => res,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('billx_token')
      localStorage.removeItem('billx_user')
      window.location.href = '/login'
    } else if (error.response?.status === 403) {
      toast.error('Access denied')
    }
    return Promise.reject(error)
  },
)

export default api
