import api from '../api/axios'
import { UserDto, StandardResponse } from '../types'

export const employeeService = {
  getByStore: async (storeId: number) =>
    (await api.get<StandardResponse<UserDto[]>>(`/api/employees/store/${storeId}`)).data.data,

  getByBranch: async (branchId: number) =>
    (await api.get<StandardResponse<UserDto[]>>(`/api/employees/branch/${branchId}`)).data.data,

  createForStore: async (storeId: number, data: Partial<UserDto> & { password: string }) =>
    (await api.post<StandardResponse<UserDto>>(`/api/employees/store/${storeId}`, data)).data.data,

  update: async (id: number, data: Partial<UserDto>) =>
    (await api.put<StandardResponse<UserDto>>(`/api/employees/${id}`, data)).data.data,

  delete: async (id: number) =>
    (await api.delete(`/api/employees/${id}`)).data,
}
