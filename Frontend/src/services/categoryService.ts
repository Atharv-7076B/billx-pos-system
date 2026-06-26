import api from '../api/axios'
import { CategoryDto } from '../types'

export const categoryService = {
  getByStore: async (storeId: number) =>
    (await api.get<CategoryDto[]>(`/api/categories/store/${storeId}`)).data,

  create: async (data: CategoryDto) =>
    (await api.post<CategoryDto>('/api/categories', data)).data,

  update: async (id: number, data: CategoryDto) =>
    (await api.put<CategoryDto>(`/api/categories/${id}`, data)).data,

  delete: async (id: number) =>
    (await api.delete(`/api/categories/${id}`)).data,
}
