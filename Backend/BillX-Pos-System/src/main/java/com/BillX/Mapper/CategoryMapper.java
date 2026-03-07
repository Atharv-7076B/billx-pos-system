package com.BillX.Mapper;

import com.BillX.Model.Category;
import com.BillX.Payload.dto.CategoryDto;

public class CategoryMapper {

    public static CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .name(category.getName())
                .storeId(category.getStore()!=null? category.getStore().getId() : null)
                .build();
    }
}
