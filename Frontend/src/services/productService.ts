import api from '../api/axios'
import { ProductDto } from '../types'

export const productService = {
  getByStore: async (storeId: number) =>
    (await api.get<ProductDto[]>(`/api/products/store/${storeId}`)).data,

  search: async (storeId: number, keyword: string) =>
    (await api.get<ProductDto[]>(`/api/products/store/${storeId}/search`, { params: { keyword } })).data,

  create: async (storeId: number, categoryId: number, data: ProductDto) =>
    (await api.post<ProductDto>(`/api/products/store/${storeId}/category/${categoryId}`, data)).data,

  update: async (id: number, data: ProductDto, jwt: string) =>
    (await api.patch<ProductDto>(`/api/products/update/${id}`, data, { headers: { Authorization: `Bearer ${jwt}` } })).data,

  delete: async (id: number, jwt: string) =>
    (await api.delete(`/api/products/delete/${id}`, { headers: { Authorization: `Bearer ${jwt}` } })).data,
}
