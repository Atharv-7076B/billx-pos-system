package com.BillX.Payload.dto;

import com.BillX.Model.StoreContact;
import com.BillX.domain.StoreStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StoreDto {
    private Long id;
    private String brand;
    private UserDto storeAdmin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String description;
    private String storeType;
    private StoreStatus status;
    private StoreContact storeContact;
}
