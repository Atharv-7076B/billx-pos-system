import api from '../api/axios'
import { InventoryDto } from '../types'

export const inventoryService = {
  getByBranch: async (branchId: number) =>
    (await api.get<InventoryDto[]>(`/api/inventory/branch/${branchId}`)).data,

  getById: async (id: number) =>
    (await api.get<InventoryDto>(`/api/inventory/${id}`)).data,

  create: async (data: InventoryDto) =>
    (await api.post<InventoryDto>('/api/inventory', data)).data,

  update: async (id: number, data: Partial<InventoryDto>) =>
    (await api.put<InventoryDto>(`/api/inventory/${id}`, data)).data,

  delete: async (id: number) =>
    (await api.delete(`/api/inventory/${id}`)).data,
}
