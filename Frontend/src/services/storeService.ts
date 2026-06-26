import api from '../api/axios'
import { StoreDto, StandardResponse } from '../types'

export const storeService = {
  getAll: async () => (await api.get<StoreDto[]>('/api/store/all')).data,
  getById: async (id: number) => (await api.get<StoreDto>(`/api/store/${id}`)).data,
  getByAdmin: async () => (await api.get<StoreDto>('/api/store/admin')).data,
  create: async (data: StoreDto) => (await api.post<StoreDto>('/api/store/create', data)).data,
  update: async (id: number, data: StoreDto) => (await api.put<StoreDto>(`/api/store/${id}`, data)).data,
}
