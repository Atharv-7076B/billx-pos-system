package com.BillX.controller;

import com.BillX.Payload.dto.DashboardStatsDto;
import com.BillX.Payload.response.StandardResponse;
import com.BillX.Service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Dashboard analytics APIs")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/store/{storeId}")
    @Operation(summary = "Get dashboard statistics for a store")
    public ResponseEntity<StandardResponse<DashboardStatsDto>> getStats(@PathVariable Long storeId) throws Exception {
        return ResponseEntity.ok(StandardResponse.success(dashboardService.getDashboardStats(storeId)));
    }
}
