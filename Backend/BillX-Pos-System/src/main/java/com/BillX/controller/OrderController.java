package com.BillX.controller;

import com.BillX.Model.User;
import com.BillX.Payload.dto.OrderDto;
import com.BillX.Payload.request.CreateOrderRequest;
import com.BillX.Payload.response.StandardResponse;
import com.BillX.Service.OrderService;
import com.BillX.Service.UserService;
import com.BillX.domain.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@Tag(name = "Orders / Billing", description = "POS Billing and Order management APIs")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create a new order (POS checkout)")
    public ResponseEntity<StandardResponse<OrderDto>> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @RequestHeader("Authorization") String jwt) throws Exception {
        User cashier = userService.getUserFromJwt(jwt);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.success("Order created", orderService.createOrder(request, cashier)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<StandardResponse<OrderDto>> getById(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(StandardResponse.success(orderService.getOrderById(id)));
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get order by order number")
    public ResponseEntity<StandardResponse<OrderDto>> getByNumber(@PathVariable String orderNumber) throws Exception {
        return ResponseEntity.ok(StandardResponse.success(orderService.getOrderByNumber(orderNumber)));
    }

    @GetMapping("/store/{storeId}")
    @Operation(summary = "Get all orders by store")
    public ResponseEntity<StandardResponse<List<OrderDto>>> getByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(StandardResponse.success(orderService.getOrdersByStore(storeId)));
    }

    @GetMapping("/store/{storeId}/recent")
    @Operation(summary = "Get recent orders")
    public ResponseEntity<StandardResponse<List<OrderDto>>> getRecent(@PathVariable Long storeId) {
        return ResponseEntity.ok(StandardResponse.success(orderService.getRecentOrders(storeId)));
    }

    @GetMapping("/store/{storeId}/search")
    @Operation(summary = "Search orders")
    public ResponseEntity<StandardResponse<List<OrderDto>>> search(
            @PathVariable Long storeId,
            @RequestParam String query) {
        return ResponseEntity.ok(StandardResponse.success(orderService.searchOrders(storeId, query)));
    }

    @GetMapping("/branch/{branchId}")
    @Operation(summary = "Get orders by branch")
    public ResponseEntity<StandardResponse<List<OrderDto>>> getByBranch(@PathVariable Long branchId) {
        return ResponseEntity.ok(StandardResponse.success(orderService.getOrdersByBranch(branchId)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<StandardResponse<OrderDto>> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) throws Exception {
        return ResponseEntity.ok(StandardResponse.success("Status updated", orderService.updateOrderStatus(id, status)));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel an order")
    public ResponseEntity<StandardResponse<OrderDto>> cancel(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(StandardResponse.success("Order cancelled", orderService.cancelOrder(id)));
    }
}
