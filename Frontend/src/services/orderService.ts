import api from '../api/axios'
import { OrderDto, CreateOrderRequest, StandardResponse, OrderStatus } from '../types'

export const orderService = {
  getByStore: async (storeId: number) =>
    (await api.get<StandardResponse<OrderDto[]>>(`/api/orders/store/${storeId}`)).data.data,

  getById: async (id: number) =>
    (await api.get<StandardResponse<OrderDto>>(`/api/orders/${id}`)).data.data,

  getRecent: async (storeId: number) =>
    (await api.get<StandardResponse<OrderDto[]>>(`/api/orders/store/${storeId}/recent`)).data.data,

  create: async (data: CreateOrderRequest) =>
    (await api.post<StandardResponse<OrderDto>>('/api/orders', data)).data.data,

  updateStatus: async (id: number, status: OrderStatus) =>
    (await api.patch<StandardResponse<OrderDto>>(`/api/orders/${id}/status`, null, { params: { status } })).data.data,

  cancel: async (id: number) =>
    (await api.post<StandardResponse<OrderDto>>(`/api/orders/${id}/cancel`)).data.data,

  search: async (storeId: number, query: string) =>
    (await api.get<StandardResponse<OrderDto[]>>(`/api/orders/store/${storeId}/search`, { params: { query } })).data.data,
}
