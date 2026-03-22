package com.BillX.Payload.dto;

import com.BillX.Model.Branch;
import com.BillX.Model.Product;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InventoryDto {
    Long id;
    private BranchDto branch;
    private Long branchId;
    private ProductDto product;
    private Long productId;
    private Integer quantity;
    private LocalDateTime lastUpdate;
}
