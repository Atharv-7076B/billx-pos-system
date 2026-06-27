package com.BillX.Service.impl;

import com.BillX.Exception.UserException;
import com.BillX.Mapper.StoreMapper;
import com.BillX.Model.Store;
import com.BillX.Model.User;
import com.BillX.Payload.dto.StoreDto;
import com.BillX.Repository.StoreRepository;
import com.BillX.Service.StoreService;
import com.BillX.Service.UserService;
import com.BillX.domain.StoreStatus;
import com.BillX.domain.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final UserService userService;
    private final StoreMapper storeMapper;

    @Override
    @Transactional
    public StoreDto createStore(StoreDto storeDto) throws UserException {

        User admin = userService.getCurrentUsers();

        if (admin == null) {
            throw new UserException("Unauthorized");
        }

        if (admin.getRole() != UserRole.ROLE_STORE_MANAGER
                && admin.getRole() != UserRole.ROLE_ADMIN) {
            throw new UserException("You are not authorized to create store");
        }

        if (storeRepository.findByStoreAdminId(admin.getId()) != null) {
            throw new UserException("Store already exists for this user");
        }

        Store store = storeMapper.toEntity(storeDto, admin);
        return storeMapper.toDto(storeRepository.save(store));
    }

    @Override
    public StoreDto getStoreById(Long id) throws Exception {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new Exception("Store not found"));
        return storeMapper.toDto(store);
    }

    @Override
    @Transactional
    public List<StoreDto> getAllStores() {
        return storeRepository.findAll()
                .stream()
                .map(storeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public StoreDto getStoreByAdmin() throws UserException {
        User admin = userService.getCurrentUsers();
        Store store = storeRepository.findByStoreAdminId(admin.getId());
        if (store == null) {
            throw new UserException("Store not found");
        }
        return storeMapper.toDto(store);
    }

    @Override
    public StoreDto getStoreByEmployee() throws UserException {
        User user = userService.getCurrentUsers();
        if (user.getStore() == null) {
            throw new UserException("Store not assigned");
        }
        return storeMapper.toDto(user.getStore());
    }

    @Override
    public StoreDto updateStore(Long id, StoreDto storeDto) throws UserException {

        User admin = userService.getCurrentUsers();

        if (admin.getRole() != UserRole.ROLE_STORE_MANAGER
                && admin.getRole() != UserRole.ROLE_ADMIN) {
            throw new UserException("Not authorized to update store");
        }

        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new UserException("Store not found"));

        store.setBrand(storeDto.getBrand());
        store.setDescription(storeDto.getDescription());
        store.setStoreType(storeDto.getStoreType());
        store.setStoreContact(storeDto.getStoreContact());

        return storeMapper.toDto(storeRepository.save(store));
    }

    @Override
    public StoreDto moderateStore(Long id, StoreStatus storeStatus) throws Exception {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new Exception("Store not found"));
        store.setStatus(storeStatus);
        return storeMapper.toDto(storeRepository.save(store));
    }

    @Override
    public void deleteStore(Long id) throws UserException {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new UserException("Store not found"));
        storeRepository.delete(store);
    }
}
