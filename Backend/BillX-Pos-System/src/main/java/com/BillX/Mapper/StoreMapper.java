package com.BillX.Mapper;

import com.BillX.Model.Store;
import com.BillX.Model.User;
import com.BillX.Payload.dto.StoreDto;
import org.springframework.stereotype.Component;

@Component
public class StoreMapper {


    public Store toEntity(StoreDto dto, User admin) {
        Store store = new Store();
        store.setBrand(dto.getBrand());
        store.setDescription(dto.getDescription());
        store.setStoreType(dto.getStoreType());
        store.setStoreContact(dto.getStoreContact());
        store.setStoreAdmin(admin);
        
        // Set bidirectional relationship
        admin.setStore(store);
        
        return store;
    }

    public StoreDto toDto(Store store) {
        StoreDto dto = new StoreDto();
        dto.setId(store.getId());
        dto.setBrand(store.getBrand());
        dto.setDescription(store.getDescription());
        dto.setStoreType(store.getStoreType());
        dto.setStoreContact(store.getStoreContact());
        dto.setStatus(store.getStatus());
        dto.setCreatedAt(store.getCreatedAt());
        dto.setUpdatedAt(store.getUpdatedAt());

        if (store.getStoreAdmin() != null) {
            dto.setStoreAdmin(UserMapper.toDTO(store.getStoreAdmin()));
        }

        return dto;
    }
}
