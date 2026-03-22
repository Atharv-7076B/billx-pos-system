package com.BillX.Mapper;

import com.BillX.Model.Category;
import com.BillX.Model.Product;
import com.BillX.Model.Store;
import com.BillX.Payload.dto.ProductDto;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
//    public static ProductDto toDto(Product product){
//        return ProductDto.builder()
//                .id(product.getId())
//                .name(product.getName())
//                .squ(product.getSqu())
//                .description(product.getDescription())
//                .mrp(product.getMrp())
//                .sellingPrice(product.getSellingPrice())
//                .brand(product.getBrand())
//                .category(CategoryMapper.toDto(product.getCategory()))
//                .storeId(product.getStore()!=null?product.getStore().getId():null)
//                .image(product.getImage())
//                .createdAt(product.getCreatedAt())
//                .updatedAt(product.getUpdatedAt())
//                .build();
//
//        //.catogoryId(product.getId())
//
//    }
    public static ProductDto toDto(Product product){
    return ProductDto.builder()
            .id(product.getId())
            .name(product.getName())
            .sku(product.getSku())
            .description(product.getDescription())
            .mrp(product.getMrp())
            .sellingPrice(product.getSellingPrice())
            .brand(product.getBrand())
            .category(CategoryMapper.toDto(product.getCategory()))
            .storeId(product.getStore()!=null ? product.getStore().getId() : null)
            .image(product.getImage())
            .createdAt(product.getCreatedAt())
            .updatedAt(product.getUpdatedAt())
            .build();
}
    public static Product toEntity(ProductDto productDto, Store store, Category category){
        return Product.builder()
                .name(productDto.getName())
                .sku(productDto.getSku())
                .store(store)
                .category(category)
                .description(productDto.getDescription())
                .mrp(productDto.getMrp())
                .sellingPrice(productDto.getSellingPrice())
                .brand(productDto.getBrand())
                .build();
    }
}
