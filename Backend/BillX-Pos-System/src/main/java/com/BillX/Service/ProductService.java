package com.BillX.Service;

import com.BillX.Model.User;
import com.BillX.Payload.dto.ProductDto;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProductService {
    ProductDto createProduct(ProductDto productDto, Long storeId, Long categoryId) throws Exception;
    ProductDto updateProduct(Long id,ProductDto productDto,User user) throws Exception;
    void deleteProduct(Long id,User user) throws Exception;
    List<ProductDto> getAllProducts(Long storeId);
    List<ProductDto> searchByKeyword(Long storeId,String keyword);
    List<ProductDto> getProductsById(Long id) throws Exception;
}
