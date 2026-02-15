package com.BillX.Service;

import com.BillX.Exception.UserException;
import com.BillX.Payload.dto.StoreDto;
import com.BillX.domain.StoreStatus;

import java.util.List;

public interface StoreService {

    StoreDto createStore(StoreDto storeDto) throws UserException;

    StoreDto getStoreById(Long id) throws Exception;

    List<StoreDto> getAllStores();

    StoreDto getStoreByAdmin() throws UserException;

    StoreDto getStoreByEmployee() throws UserException;

    StoreDto updateStore(Long id, StoreDto storeDto) throws UserException;

    StoreDto moderateStore(Long id, StoreStatus storeStatus) throws Exception;

    void deleteStore(Long id) throws UserException;
}
