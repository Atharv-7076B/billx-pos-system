package com.BillX.Service.impl;

import com.BillX.Exception.UserException;
import com.BillX.Mapper.CategoryMapper;
import com.BillX.Model.Category;
import com.BillX.Model.Store;
import com.BillX.Model.User;
import com.BillX.Payload.dto.CategoryDto;
import com.BillX.Payload.dto.StoreDto;
import com.BillX.Repository.CategoryRepository;
import com.BillX.Repository.StoreRepository;
import com.BillX.Service.CategoryService;
import com.BillX.Service.StoreService;
import com.BillX.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final StoreRepository storeRepository;
    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) throws Exception {
        User user = userService.getCurrentUsers();
        Store store = storeRepository.findById(categoryDto.getId()).orElseThrow(
                ()-> new Exception("Store Not Found")
        );
        Category category =Category.builder()
                .store(store)
                .name(categoryDto.getName())
                .build();

        return CategoryMapper.toDto(category);
    }

    @Override
    public List<CategoryDto> getCategoriesByStore(Long storeId) {
        return List.of();
    }

    @Override
    public CategoryDto updateCategory(Long Id, CategoryDto categoryDto) {
        return null;
    }

    @Override
    public void deleteCategory(Long Id) {

    }
}
