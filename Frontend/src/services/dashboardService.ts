import api from '../api/axios'
import { DashboardStatsDto, StandardResponse } from '../types'

export const dashboardService = {
  getStats: async (storeId: number) =>
    (await api.get<StandardResponse<DashboardStatsDto>>(`/api/dashboard/store/${storeId}`)).data.data,
}
