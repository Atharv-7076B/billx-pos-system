package com.BillX.Payload.dto;

import com.BillX.Model.Store;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ProductDto {
    private Long id;

    private String name;

    private String squ;

    private String description;
    private double mrp;
    private double sellingPrice;
    private String brand;
    private String image;
    //private Category category;
    private Long catogoryId;
    private Long storeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
