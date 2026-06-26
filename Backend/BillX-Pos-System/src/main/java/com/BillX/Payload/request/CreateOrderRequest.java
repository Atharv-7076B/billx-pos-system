package com.BillX.Payload.request;

import com.BillX.domain.PaymentMethod;
import com.BillX.Payload.dto.OrderItemDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {

    @NotNull(message = "Store ID is required")
    private Long storeId;

    private Long branchId;
    private Long customerId;

    @NotEmpty(message = "Order must have at least one item")
    @Valid
    private List<OrderItemDto> items;

    private double discountAmount;
    private double taxPercent;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private String notes;
    private boolean deductInventory = true;
}
