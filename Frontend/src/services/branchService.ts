import api from '../api/axios'
import { BranchDto } from '../types'

export const branchService = {
  getByStore: async (storeId: number) =>
    (await api.get<BranchDto[]>(`/api/branches/store/${storeId}`)).data,

  getById: async (id: number) =>
    (await api.get<BranchDto>(`/api/branches/${id}`)).data,

  create: async (data: BranchDto) =>
    (await api.post<BranchDto>('/api/branches', data)).data,

  update: async (id: number, data: BranchDto) =>
    (await api.put<BranchDto>(`/api/branches/${id}`, data)).data,

  delete: async (id: number) =>
    (await api.delete(`/api/branches/${id}`)).data,
}
