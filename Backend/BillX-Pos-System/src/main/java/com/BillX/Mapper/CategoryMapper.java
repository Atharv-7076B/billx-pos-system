package com.BillX.Mapper;

import com.BillX.Model.Category;
import com.BillX.Payload.dto.CategoryDto;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public static CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .storeId(category.getStore()!=null? category.getStore().getId() : null)
                .build();
    }
}
