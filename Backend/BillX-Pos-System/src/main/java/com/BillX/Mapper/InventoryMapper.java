package com.BillX.Mapper;

import com.BillX.Model.Branch;
import com.BillX.Model.Inventory;
import com.BillX.Model.Product;
import com.BillX.Payload.dto.InventoryDto;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {
    public static InventoryDto toDto(Inventory inventory){
        return InventoryDto.builder()
                .id(inventory.getId())
                .branchId(inventory.getBranch().getId())
                .productId(inventory.getProduct().getId())
                .product(ProductMapper.toDto(inventory.getProduct()))
                .quantity(inventory.getQuantity()).build();
    }

    public static Inventory toEntity(InventoryDto inventoryDto, Branch branch, Product product){
        return Inventory.builder()
                .id(inventoryDto.getId())
                .branch(branch)
                .product(product)
                .quantity(inventoryDto.getQuantity())
                .build();
    }
}
