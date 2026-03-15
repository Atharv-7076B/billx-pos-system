package com.BillX.controller;

import com.BillX.Payload.dto.CategoryDto;
import com.BillX.Payload.response.ApiResponse;
import com.BillX.Service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<CategoryDto> createCategory(
            @RequestBody CategoryDto categoryDto) throws Exception {
        return ResponseEntity.ok(
                categoryService.createCategory(categoryDto)
        );
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<CategoryDto>> getCategoriesByStoreId(
            @PathVariable Long storeId)
            throws Exception {
        return ResponseEntity.ok(
                categoryService.getCategoriesByStore(storeId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(
            @RequestBody CategoryDto categoryDto,
            @PathVariable Long id)
            throws Exception {
        return ResponseEntity.ok(
                categoryService.updateCategory(id,categoryDto)
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(
            @PathVariable Long id)
            throws Exception {
        categoryService.deleteCategory(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Category Deleted Successfully");
        return ResponseEntity.ok(
                apiResponse
        );
    }
}
