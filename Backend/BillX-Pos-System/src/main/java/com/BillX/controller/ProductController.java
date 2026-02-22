package com.BillX.controller;

import com.BillX.Model.User;
import com.BillX.Payload.dto.ProductDto;
import com.BillX.Payload.response.ApiResponse;
import com.BillX.Service.ProductService;
import com.BillX.Service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final UserService userService;


    @PostMapping("/create")
    public ResponseEntity<ProductDto> create(@RequestBody ProductDto productDto,
                                             @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.getUserFromJwt(jwt);
        return ResponseEntity.ok(
                productService.createProduct(productDto,user)
        );
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<ProductDto>> getByStoreId(@PathVariable Long storeId,
                                                         @RequestHeader("Authorization") String jwt) throws Exception {
        return ResponseEntity.ok(
                productService.getProductsById(storeId)
        );
    }

    @PatchMapping("/update/{productId}")
    public ResponseEntity<ProductDto> update(@PathVariable Long productId,
                                             @RequestBody ProductDto productDto,
                                             @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.getUserFromJwt(jwt);
        return ResponseEntity.ok(
                productService.updateProduct(productId,productDto,user)
        );
    }

    @GetMapping("/store/{storeId}/search")
    public ResponseEntity<List<ProductDto>> searchByKeyword(@PathVariable Long storeId,
                                                            @RequestParam String keyword,
                                                            @RequestHeader("Authorization") String jwt) throws Exception {
        return ResponseEntity.ok(
                productService.searchByKeyword(
                        storeId
                        ,keyword
                )
        );
    }
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long productId,
                                              @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.getUserFromJwt(jwt);
        productService.deleteProduct(productId,user);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Product deleted successfully");
        return ResponseEntity.ok(
            apiResponse
        );
    }
}
