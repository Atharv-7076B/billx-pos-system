package com.BillX.Service.impl;

import com.BillX.Payload.dto.DashboardStatsDto;
import com.BillX.Repository.*;
import com.BillX.Service.DashboardService;
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
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public DashboardStatsDto getDashboardStats(Long storeId) throws Exception {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        double todaySales = orderRepository.sumTotalByStoreAndDateRange(storeId, todayStart, todayEnd);
        double monthlySales = orderRepository.sumTotalByStoreAndDateRange(storeId, monthStart, todayEnd);
        double totalRevenue = orderRepository.sumTotalByStoreAndDateRange(storeId,
                LocalDateTime.of(2000, 1, 1, 0, 0), todayEnd);

        long todayOrders = orderRepository.countByStoreAndDateRange(storeId, todayStart, todayEnd);
        long totalOrders = orderRepository.countByStoreId(storeId);
        long totalProducts = productRepository.findByStoreId(storeId).size();
        long totalCustomers = customerRepository.countByStoreId(storeId);

        List<com.BillX.Model.Inventory> lowStockList = inventoryRepository.findAll()
                .stream()
                .filter(inv -> inv.getBranch() != null
                        && inv.getBranch().getStore() != null
                        && inv.getBranch().getStore().getId().equals(storeId)
                        && inv.getQuantity() <= 10)
                .collect(Collectors.toList());

        List<Map<String, Object>> recentOrders = orderRepository
                .findTop10ByStoreIdOrderByCreatedAtDesc(storeId)
                .stream()
                .map(o -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", o.getId());
                    m.put("orderNumber", o.getOrderNumber());
                    m.put("total", o.getTotal());
                    m.put("status", o.getOrderStatus());
                    m.put("paymentMethod", o.getPaymentMethod());
                    m.put("customerName", o.getCustomer() != null ? o.getCustomer().getName() : "Walk-in");
                    m.put("createdAt", o.getCreatedAt());
                    return m;
                }).collect(Collectors.toList());

        List<Map<String, Object>> salesChart = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = LocalDate.now().minusDays(i);
            double sales = orderRepository.sumTotalByStoreAndDateRange(storeId,
                    day.atStartOfDay(), day.atTime(LocalTime.MAX));
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("date", day.toString());
            point.put("sales", sales);
            salesChart.add(point);
        }

        List<Map<String, Object>> lowStockAlerts = lowStockList.stream()
                .map(inv -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("productId", inv.getProduct().getId());
                    m.put("productName", inv.getProduct().getName());
                    m.put("sku", inv.getProduct().getSku());
                    m.put("quantity", inv.getQuantity());
                    m.put("branchName", inv.getBranch().getName());
                    return m;
                }).collect(Collectors.toList());

        return DashboardStatsDto.builder()
                .totalRevenue(totalRevenue)
                .todaySales(todaySales)
                .monthlySales(monthlySales)
                .totalOrders(totalOrders)
                .todayOrders(todayOrders)
                .totalProducts(totalProducts)
                .totalCustomers(totalCustomers)
                .lowStockItems(lowStockList.size())
                .recentOrders(recentOrders)
                .salesChartData(salesChart)
                .lowStockAlerts(lowStockAlerts)
                .build();
    }
}
