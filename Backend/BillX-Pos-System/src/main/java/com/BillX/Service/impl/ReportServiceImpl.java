package com.BillX.Service.impl;

import com.BillX.Model.Inventory;
import com.BillX.Payload.dto.OrderDto;
import com.BillX.Repository.*;
import com.BillX.Service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public Map<String, Object> getDailySalesReport(Long storeId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        var orders = orderRepository.findByStoreIdAndDateRange(storeId, start, end);
        double total = orders.stream().mapToDouble(o -> o.getTotal()).sum();
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("date", date.toString());
        report.put("totalOrders", orders.size());
        report.put("totalRevenue", total);
        report.put("orders", orders.stream().map(com.BillX.Mapper.OrderMapper::toDto).collect(Collectors.toList()));
        return report;
    }

    @Override
    public Map<String, Object> getMonthlySalesReport(Long storeId, int year, int month) {
        LocalDateTime start = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime end = LocalDate.of(year, month, 1).withDayOfMonth(
                LocalDate.of(year, month, 1).lengthOfMonth()).atTime(LocalTime.MAX);
        var orders = orderRepository.findByStoreIdAndDateRange(storeId, start, end);
        double total = orders.stream().mapToDouble(o -> o.getTotal()).sum();

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("year", year);
        report.put("month", month);
        report.put("totalOrders", orders.size());
        report.put("totalRevenue", total);

        Map<Integer, Double> dailyBreakdown = new LinkedHashMap<>();
        for (var order : orders) {
            int day = order.getCreatedAt().getDayOfMonth();
            dailyBreakdown.merge(day, order.getTotal(), Double::sum);
        }
        report.put("dailyBreakdown", dailyBreakdown);
        return report;
    }

    @Override
    public Map<String, Object> getProductSalesReport(Long storeId, LocalDate from, LocalDate to) {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(LocalTime.MAX);
        List<Object[]> raw = orderItemRepository.findTopProductsByStoreAndDateRange(storeId, start, end);

        List<Map<String, Object>> products = raw.stream().map(row -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("productId", row[0]);
            m.put("productName", row[1]);
            m.put("totalQuantity", row[2]);
            m.put("totalRevenue", row[3]);
            return m;
        }).collect(Collectors.toList());

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("from", from.toString());
        report.put("to", to.toString());
        report.put("products", products);
        return report;
    }

    @Override
    public Map<String, Object> getInventoryReport(Long storeId) {
        List<Inventory> inventories = inventoryRepository.findAll().stream()
                .filter(inv -> inv.getBranch() != null
                        && inv.getBranch().getStore() != null
                        && inv.getBranch().getStore().getId().equals(storeId))
                .collect(Collectors.toList());

        List<Map<String, Object>> items = inventories.stream().map(inv -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("productId", inv.getProduct().getId());
            m.put("productName", inv.getProduct().getName());
            m.put("sku", inv.getProduct().getSku());
            m.put("quantity", inv.getQuantity());
            m.put("branchId", inv.getBranch().getId());
            m.put("branchName", inv.getBranch().getName());
            m.put("lowStock", inv.getQuantity() <= 10);
            m.put("lastUpdate", inv.getLastUpdate());
            return m;
        }).collect(Collectors.toList());

        long lowStock = items.stream().filter(m -> (Boolean) m.get("lowStock")).count();

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("totalItems", inventories.size());
        report.put("lowStockItems", lowStock);
        report.put("inventory", items);
        return report;
    }

    @Override
    public List<Map<String, Object>> getSalesChartData(Long storeId, int days) {
        List<Map<String, Object>> chart = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate day = LocalDate.now().minusDays(i);
            double sales = orderRepository.sumTotalByStoreAndDateRange(storeId,
                    day.atStartOfDay(), day.atTime(LocalTime.MAX));
            long count = orderRepository.countByStoreAndDateRange(storeId,
                    day.atStartOfDay(), day.atTime(LocalTime.MAX));
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("date", day.toString());
            point.put("sales", sales);
            point.put("orders", count);
            chart.add(point);
        }
        return chart;
    }
}
