package com.BillX.Payload.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemDto {
    private Long id;

    @NotNull(message = "Product ID is required")
    private Long productId;

    private String productName;
    private String productSku;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @Min(value = 0, message = "Unit price cannot be negative")
    private double unitPrice;

    private double discountAmount;
    private double total;
}
