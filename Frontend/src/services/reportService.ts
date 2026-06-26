import api from '../api/axios'
import { StandardResponse } from '../types'

export const reportService = {
  getDaily: async (storeId: number, date?: string) =>
    (await api.get<StandardResponse<Record<string, unknown>>>(`/api/reports/store/${storeId}/daily`, { params: { date } })).data.data,

  getMonthly: async (storeId: number, year?: number, month?: number) =>
    (await api.get<StandardResponse<Record<string, unknown>>>(`/api/reports/store/${storeId}/monthly`, { params: { year, month } })).data.data,

  getProductReport: async (storeId: number, from?: string, to?: string) =>
    (await api.get<StandardResponse<Record<string, unknown>>>(`/api/reports/store/${storeId}/products`, { params: { from, to } })).data.data,

  getInventoryReport: async (storeId: number) =>
    (await api.get<StandardResponse<Record<string, unknown>>>(`/api/reports/store/${storeId}/inventory`)).data.data,

  getChartData: async (storeId: number, days = 30) =>
    (await api.get<StandardResponse<Array<Record<string, unknown>>>>(`/api/reports/store/${storeId}/chart`, { params: { days } })).data.data,
}
