package com.BillX.Payload.dto;

import com.BillX.Model.Store;
import com.BillX.Model.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class BranchDto {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private List<String> workingDays;
    private LocalTime openTime;
    private LocalTime closeTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Store store;
    private Long storeId;
    private User manager;
}
