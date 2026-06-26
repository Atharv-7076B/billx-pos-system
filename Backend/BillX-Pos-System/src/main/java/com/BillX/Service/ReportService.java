package com.BillX.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public interface ReportService {
    Map<String, Object> getDailySalesReport(Long storeId, LocalDate date);
    Map<String, Object> getMonthlySalesReport(Long storeId, int year, int month);
    Map<String, Object> getProductSalesReport(Long storeId, LocalDate from, LocalDate to);
    Map<String, Object> getInventoryReport(Long storeId);
    List<Map<String, Object>> getSalesChartData(Long storeId, int days);
}
