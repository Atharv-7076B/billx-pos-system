package com.BillX.Service;

import com.BillX.Exception.UserException;
import com.BillX.Model.Store;
import com.BillX.Model.User;
import com.BillX.Payload.dto.StoreDto;

import java.util.List;

public interface StoreService {
    StoreDto createStore(StoreDto storeDto, User user);
    StoreDto getStoreById(Long id) throws Exception;
    List<StoreDto> getAllStores();
    Store getStoreByAdmin() throws UserException;
    StoreDto updateStore(Long id, StoreDto storeDto);
    StoreDto deleteStore(Long id);
    StoreDto getStoreByEmployee() throws UserException;
}
