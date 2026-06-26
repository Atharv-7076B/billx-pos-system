package com.BillX.Payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Long id;

    private String name;

//    private Store store;
    private Long storeId;
}
