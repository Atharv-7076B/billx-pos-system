package com.BillX.Service;

import com.BillX.Exception.UserException;
import com.BillX.Model.Store;
import com.BillX.Model.User;
import com.BillX.Payload.dto.StoreDto;
import com.BillX.domain.StoreStatus;

import java.util.List;

public interface StoreService {
    StoreDto createStore(StoreDto storeDto, User user);
    StoreDto getStoreById(Long id) throws Exception;
    List<StoreDto> getAllStores();
    Store getStoreByAdmin() throws UserException;
    StoreDto updateStore(Long id, StoreDto storeDto) throws UserException;
    void deleteStore(Long id) throws UserException;
    Object getStoreByEmployee() throws UserException;
    StoreDto moderateStore(Long id, StoreStatus storeStatus) throws Exception;
}
