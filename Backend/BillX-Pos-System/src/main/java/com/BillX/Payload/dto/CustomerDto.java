package com.BillX.Payload.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerDto {
    private Long id;

    @NotBlank(message = "Customer name is required")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    private String phone;
    private String address;
    private Long storeId;
    private double totalPurchases;
    private int totalOrders;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
