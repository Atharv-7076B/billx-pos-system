package com.BillX.Service;

import com.BillX.Model.User;
import com.BillX.Payload.dto.ProductDto;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProductService {
    ProductDto createProduct(ProductDto productDto, User user);
    ProductDto updateProduct(Long id,ProductDto productDto,User user);
    void deleteProduct(Long id,User user);
    List<ProductDto> getAllProducts(Long storeId);
    List<ProductDto> searchByKeyword(Long storeId,String keyword);
}
