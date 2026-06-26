package com.BillX.controller;

import com.BillX.Payload.response.StandardResponse;
import com.BillX.Service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
@Tag(name = "Reports", description = "Sales and Inventory report APIs")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/store/{storeId}/daily")
    @Operation(summary = "Daily sales report")
    public ResponseEntity<StandardResponse<Map<String, Object>>> dailyReport(
            @PathVariable Long storeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) date = LocalDate.now();
        return ResponseEntity.ok(StandardResponse.success(reportService.getDailySalesReport(storeId, date)));
    }

    @GetMapping("/store/{storeId}/monthly")
    @Operation(summary = "Monthly sales report")
    public ResponseEntity<StandardResponse<Map<String, Object>>> monthlyReport(
            @PathVariable Long storeId,
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "0") int month) {
        if (year == 0) year = LocalDate.now().getYear();
        if (month == 0) month = LocalDate.now().getMonthValue();
        return ResponseEntity.ok(StandardResponse.success(reportService.getMonthlySalesReport(storeId, year, month)));
    }

    @GetMapping("/store/{storeId}/products")
    @Operation(summary = "Product sales report")
    public ResponseEntity<StandardResponse<Map<String, Object>>> productReport(
            @PathVariable Long storeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        if (from == null) from = LocalDate.now().minusDays(30);
        if (to == null) to = LocalDate.now();
        return ResponseEntity.ok(StandardResponse.success(reportService.getProductSalesReport(storeId, from, to)));
    }

    @GetMapping("/store/{storeId}/inventory")
    @Operation(summary = "Inventory report")
    public ResponseEntity<StandardResponse<Map<String, Object>>> inventoryReport(@PathVariable Long storeId) {
        return ResponseEntity.ok(StandardResponse.success(reportService.getInventoryReport(storeId)));
    }

    @GetMapping("/store/{storeId}/chart")
    @Operation(summary = "Sales chart data")
    public ResponseEntity<StandardResponse<List<Map<String, Object>>>> chartData(
            @PathVariable Long storeId,
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(StandardResponse.success(reportService.getSalesChartData(storeId, days)));
    }
}
