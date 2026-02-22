package com.BillX.Mapper;

import com.BillX.Model.Product;
import com.BillX.Model.Store;
import com.BillX.Payload.dto.ProductDto;

public class ProductMapper {
    public static ProductDto toDto(Product product){
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .squ(product.getSqu())
                .description(product.getDescription())
                .mrp(product.getMrp())
                .sellingPrice(product.getSellingPrice())
                .brand(product.getBrand())
                .storeId(product.getStore()!=null?product.getStore().getId():null)
                .image(product.getImage())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();

        //.catogoryId(product.getId())

    }
    public static Product toEntity(ProductDto productDto, Store store){
        return Product.builder()
                .name(productDto.getName())
                .squ(productDto.getSqu())
                .description(productDto.getDescription())
                .mrp(productDto.getMrp())
                .sellingPrice(productDto.getSellingPrice())
                .brand(productDto.getBrand())
                .build();
    }
}
