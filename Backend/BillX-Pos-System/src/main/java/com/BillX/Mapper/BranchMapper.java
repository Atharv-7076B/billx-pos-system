package com.BillX.Mapper;

import com.BillX.Model.Branch;
import com.BillX.Model.Store;
import com.BillX.Payload.dto.BranchDto;
import org.springframework.stereotype.Component;

@Component
public class BranchMapper {
    public static BranchDto toDto(Branch branch){
        return BranchDto.builder()
                .id(branch.getId())
                .name(branch.getName())
                .address(branch.getAddress())
                .email(branch.getEmail())
                .phone(branch.getPhone())
                .closeTime(branch.getCloseTime())
                .openTime(branch.getOpenTime())
                .workingDays(branch.getWorkingDays())
                .storeId(branch.getStore()!=null ? branch.getStore().getId(): null)
                .createdAt(branch.getCreatedAt())
                .updatedAt(branch.getUpdatedAt())
                .build();
    }

    public static Branch toEntity(BranchDto dto, Store store){
        return Branch.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .closeTime(dto.getCloseTime())
                .openTime(dto.getOpenTime())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }
}
