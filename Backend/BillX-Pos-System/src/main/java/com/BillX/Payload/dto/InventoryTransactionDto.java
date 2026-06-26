package com.BillX.Payload.dto;

import com.BillX.domain.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryTransactionDto {
    private Long id;

    @NotNull(message = "Inventory ID is required")
    private Long inventoryId;

    private Long productId;
    private String productName;
    private String productSku;
    private Long branchId;
    private String branchName;

    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    private int previousQuantity;
    private int newQuantity;
    private String reason;
    private Long performedById;
    private String performedByName;
    private Long orderId;
    private LocalDateTime createdAt;
}
