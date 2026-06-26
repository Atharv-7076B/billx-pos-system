package com.BillX.Payload.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardStatsDto {
    private double totalRevenue;
    private double todaySales;
    private double monthlySales;
    private long totalOrders;
    private long todayOrders;
    private long totalProducts;
    private long totalCustomers;
    private long totalEmployees;
    private long lowStockItems;
    private List<Map<String, Object>> recentOrders;
    private List<Map<String, Object>> salesChartData;
    private List<Map<String, Object>> topProducts;
    private List<Map<String, Object>> lowStockAlerts;
}
