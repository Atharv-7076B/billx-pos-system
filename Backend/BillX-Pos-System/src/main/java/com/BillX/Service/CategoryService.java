package com.BillX.Service;

import com.BillX.Payload.dto.CategoryDto;
import java.util.List;


public interface CategoryService {
    CategoryDto createCategory(CategoryDto categoryDto) throws Exception;
    List<CategoryDto> getCategoriesByStore(Long storeId);
    CategoryDto updateCategory(Long Id ,CategoryDto categoryDto) throws Exception;
    void deleteCategory(Long Id) throws Exception;
}
