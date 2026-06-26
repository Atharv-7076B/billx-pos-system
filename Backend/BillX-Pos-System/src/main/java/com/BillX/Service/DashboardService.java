package com.BillX.Service;

import com.BillX.Payload.dto.DashboardStatsDto;

public interface DashboardService {
    DashboardStatsDto getDashboardStats(Long storeId) throws Exception;
}
