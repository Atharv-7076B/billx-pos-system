import api from '../api/axios'
import { CustomerDto, OrderDto, StandardResponse } from '../types'

export const customerService = {
  getByStore: async (storeId: number) =>
    (await api.get<StandardResponse<CustomerDto[]>>(`/api/customers/store/${storeId}`)).data.data,

  search: async (storeId: number, query: string) =>
    (await api.get<StandardResponse<CustomerDto[]>>(`/api/customers/store/${storeId}/search`, { params: { query } })).data.data,

  getById: async (id: number) =>
    (await api.get<StandardResponse<CustomerDto>>(`/api/customers/${id}`)).data.data,

  create: async (data: CustomerDto) =>
    (await api.post<StandardResponse<CustomerDto>>('/api/customers', data)).data.data,

  update: async (id: number, data: CustomerDto) =>
    (await api.put<StandardResponse<CustomerDto>>(`/api/customers/${id}`, data)).data.data,

  delete: async (id: number) =>
    (await api.delete(`/api/customers/${id}`)).data,

  getPurchaseHistory: async (id: number) =>
    (await api.get<StandardResponse<OrderDto[]>>(`/api/customers/${id}/orders`)).data.data,
}
