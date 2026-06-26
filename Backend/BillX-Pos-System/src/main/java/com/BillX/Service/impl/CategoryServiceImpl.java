package com.BillX.Service.impl;

import com.BillX.Mapper.CategoryMapper;
import com.BillX.Model.Category;
import com.BillX.Model.Store;
import com.BillX.Model.User;
import com.BillX.Payload.dto.CategoryDto;
import com.BillX.Repository.CategoryRepository;
import com.BillX.Repository.StoreRepository;
import com.BillX.Service.CategoryService;
import com.BillX.Service.UserService;
import com.BillX.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final StoreRepository storeRepository;

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) throws Exception {

        User user = userService.getCurrentUsers();

        Store store = storeRepository.findById(categoryDto.getStoreId())
                .orElseThrow(() -> new Exception("Store Not Found"));

        Category category = Category.builder()
                .store(store)
                .name(categoryDto.getName())
                .build();

        checkAuthority(user, category.getStore());

        return CategoryMapper.toDto(categoryRepository.save(category));
    }
//    public CategoryDto createCategory(CategoryDto categoryDto) throws Exception {
//        User user = userService.getCurrentUsers();
//        Store store = storeRepository.findById(categoryDto.getId()).orElseThrow(
//                ()-> new Exception("Store Not Found")
//        );
//        Category category =Category.builder()
//                .store(store)
//                .name(categoryDto.getName())
//                .build();
//        checkAuthority(user,category.getStore());
//
//        return CategoryMapper.toDto(categoryRepository.save(category));
//    }

    @Override
    public List<CategoryDto> getCategoriesByStore(Long storeId) {
        List<Category> categories = (List<Category>) categoryRepository.findByStoreId(storeId);
        return categories.stream()
                .map(
                        CategoryMapper::toDto
                ).collect(Collectors.toList());
    }

    @Override
    public CategoryDto updateCategory(Long Id, CategoryDto categoryDto) throws Exception {
        Category category = categoryRepository.findById(Id).orElseThrow(
                ()-> new Exception("Category Doesn't Exist")
        );
        User user = userService.getCurrentUsers();
        category.setName(categoryDto.getName());
        checkAuthority(user,category.getStore());
        categoryRepository.save(category);

        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long Id) throws Exception {
        Category category = categoryRepository.findById(Id).orElseThrow(
                ()-> new Exception("Category Doesn't Exist")
        );
        User user = userService.getCurrentUsers();
        checkAuthority(user,category.getStore());
        categoryRepository.delete(category);
    }
    private  void checkAuthority(User user,Store store) throws Exception{
        boolean isAdmin = user.getRole().equals(UserRole.ROLE_ADMIN);
        boolean isManager = user.getRole().equals(UserRole.ROLE_STORE_MANAGER);
        boolean isSameStore = user.equals(store.getStoreAdmin());
        if(!(isAdmin && isSameStore) && !isManager ){
            throw  new Exception("You don't have access to manage this category");
        }
    }
}
