package com.BillX.Service.impl;

import com.BillX.Mapper.ProductMapper;
import com.BillX.Model.Category;
import com.BillX.Model.Product;
import com.BillX.Model.Store;
import com.BillX.Model.User;
import com.BillX.Payload.dto.ProductDto;
import com.BillX.Repository.CategoryRepository;
import com.BillX.Repository.ProductRepository;
import com.BillX.Repository.StoreRepository;
import com.BillX.Repository.UserRepository;
import com.BillX.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    @Override
    public ProductDto createProduct(ProductDto productDto, Long storeId, Long categoryId) throws Exception {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new Exception("Store Not Found"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new Exception("Category Not Found"));

        Product product = ProductMapper.toEntity(productDto, store, category);

        Product savedProduct = productRepository.save(product);

        return ProductMapper.toDto(savedProduct);
    }

    @Override
    public ProductDto updateProduct(Long id, ProductDto productDto, User user) throws Exception {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new Exception("Product Not Found"));

        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setSku(productDto.getSku());
        product.setImage(productDto.getImage());
        product.setMrp(productDto.getMrp());
        product.setSellingPrice(productDto.getSellingPrice());
        product.setBrand(productDto.getBrand());

        if (productDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDto.getCategoryId())
                    .orElseThrow(() -> new Exception("Category Not Found"));
            product.setCategory(category);
        }

        Product updatedProduct = productRepository.save(product);

        return ProductMapper.toDto(updatedProduct);
    }
//    @Override
//    public ProductDto updateProduct(Long id, ProductDto productDto, User user) throws Exception {
//        Product product = productRepository.findById(id).orElseThrow(
//                ()->new Exception("Product Not Found")
//        );
//        productDto.setName(productDto.getName());
//        productDto.setDescription(productDto.getDescription());
//        productDto.setSku(productDto.getSku());
//        productDto.setImage(product.getImage());
//        productDto.setMrp(product.getMrp());
//        productDto.setSellingPrice(product.getSellingPrice());
//        productDto.setBrand(product.getBrand());
//        productDto.setUpdatedAt(product.getUpdatedAt());
//        if(productDto.getCatogoryId() != null){
//            Category category = categoryRepository.findById(productDto.getCatogoryId()).orElseThrow(
//                    ()->new Exception("Category Not Found")
//            );
//            product.setCategory(category);
//        }
//        Product updatedProduct = productRepository.save(product);
//        return  ProductMapper.toDto(updatedProduct);
//
//    }

    @Override
    public void deleteProduct(Long id, User user) throws Exception {
        Product product = productRepository.findById(id).orElseThrow(
                ()-> new Exception("Product not found")
        );
        productRepository.delete(product);
    }

    @Override
    public List<ProductDto> getAllProducts(Long storeId) {
        List<Product> products = productRepository.findByStoreId(storeId);
        return products
                .stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public List<ProductDto> searchByKeyword(Long storeId, String keyword) {
        List<Product> products = productRepository.searchByKeyword(storeId,keyword);
        return products
                .stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getProductsById(Long id) throws Exception {
        List<Product> products = productRepository.findByStoreId(id);
        return products
                .stream()
                .map(ProductMapper ::toDto)
                .collect(Collectors.toList());
    }
}
