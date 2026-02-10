package com.BillX.Service.impl;

import com.BillX.Exception.UserException;
import com.BillX.Mapper.StoreMapper;
import com.BillX.Model.Store;
import com.BillX.Model.StoreContact;
import com.BillX.Model.User;
import com.BillX.Payload.dto.StoreDto;
import com.BillX.Repository.StoreRepository;
import com.BillX.Service.StoreService;
import com.BillX.Service.UserService;
import com.BillX.domain.StoreStatus;
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
    public StoreDto createStore(StoreDto storeDto, User user) {
         Store store = storeMapper.toEntity(storeDto,user);
         return storeMapper.toDto(storeRepository.save(store));
    }

    @Override
    public StoreDto getStoreById(Long id) throws Exception {
         Store store = storeRepository.findById(id).orElseThrow(
                 ()->new Exception("Store not found...")
         );
         return storeMapper.toDto(store);
    }

    @Override
    public List<StoreDto> getAllStores() {
        List<Store> dtos = storeRepository.findAll();
        return dtos.stream().map(StoreMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Store getStoreByAdmin() throws UserException {
        User admin = userService.getCurrentUsers();
        return storeRepository.findByAdminId(admin.getId());
    }

    @Override
    public StoreDto updateStore(Long id, StoreDto storeDto) throws UserException {
        User curretUser = userService.getCurrentUsers();
        Store existingStore = storeRepository.findByAdminId(id);
        if(existingStore == null){
            throw new UserException("Store not found...");
        }
        existingStore.setBrand(storeDto.getBrand());
        existingStore.setDescription(storeDto.getDescription());
        if(storeDto.getStoreType() == null) {
            existingStore.setStoreType(storeDto.getStoreType());
        }
        if(storeDto.getStoreContact()!=null){
            StoreContact storeContact = StoreContact.builder()
                    .address(storeDto.getStoreContact().getAddress())
                    .phoneNo(storeDto.getStoreContact().getPhoneNo())
                    .email(storeDto.getStoreContact().getEmail())
                    .build();
            existingStore.setStoreContact(storeContact);
        }
        Store updatedStore = storeRepository.save(existingStore);
        return StoreMapper.toDto(updatedStore);
    }

    @Override
    public void deleteStore(Long id) throws UserException {
        Store store = getStoreByAdmin();
        storeRepository.delete(store);
    }

    @Override
    public Object getStoreByEmployee() throws UserException {
         User currentUser = userService.getCurrentUsers();
         if (currentUser == null){
             throw new UserException("you don't have permission to acess the store");
         }
         return storeMapper.toDto(currentUser.getStore());
    }

    @Override
    public StoreDto moderateStore(Long id, StoreStatus storeStatus) throws Exception {
        Store store = storeRepository.findById(id).orElseThrow(
                ()->new Exception("Store not found")
        );
        store.setStatus(storeStatus);
        Store updatedStore = storeRepository.save(store);
        return storeMapper.toDto(updatedStore);
    }
}
