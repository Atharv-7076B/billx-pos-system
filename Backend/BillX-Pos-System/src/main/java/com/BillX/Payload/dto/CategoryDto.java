package com.BillX.Payload.dto;

import com.BillX.Model.Store;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CategoryDto {
    private Long id;

    private String name;

//    private Store store;
    private Long storeId;
}
