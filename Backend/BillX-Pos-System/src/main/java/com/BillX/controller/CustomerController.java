package com.BillX.controller;

import com.BillX.Payload.dto.CustomerDto;
import com.BillX.Payload.dto.OrderDto;
import com.BillX.Payload.response.StandardResponse;
import com.BillX.Service.CustomerService;
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
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "Customer management APIs")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "Create a new customer")
    public ResponseEntity<StandardResponse<CustomerDto>> createCustomer(
            @Valid @RequestBody CustomerDto dto) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.success("Customer created successfully", customerService.createCustomer(dto)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer")
    public ResponseEntity<StandardResponse<CustomerDto>> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerDto dto) throws Exception {
        return ResponseEntity.ok(StandardResponse.success("Customer updated", customerService.updateCustomer(id, dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer")
    public ResponseEntity<StandardResponse<Void>> deleteCustomer(@PathVariable Long id) throws Exception {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(StandardResponse.success("Customer deleted", null));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<StandardResponse<CustomerDto>> getById(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(StandardResponse.success(customerService.getCustomerById(id)));
    }

    @GetMapping("/store/{storeId}")
    @Operation(summary = "Get all customers by store")
    public ResponseEntity<StandardResponse<List<CustomerDto>>> getByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(StandardResponse.success(customerService.getCustomersByStore(storeId)));
    }

    @GetMapping("/store/{storeId}/search")
    @Operation(summary = "Search customers")
    public ResponseEntity<StandardResponse<List<CustomerDto>>> search(
            @PathVariable Long storeId,
            @RequestParam String query) {
        return ResponseEntity.ok(StandardResponse.success(customerService.searchCustomers(storeId, query)));
    }

    @GetMapping("/{customerId}/orders")
    @Operation(summary = "Get customer purchase history")
    public ResponseEntity<StandardResponse<List<OrderDto>>> getPurchaseHistory(
            @PathVariable Long customerId) throws Exception {
        return ResponseEntity.ok(StandardResponse.success(customerService.getCustomerPurchaseHistory(customerId)));
    }
}
