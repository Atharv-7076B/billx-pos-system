package com.BillX.controller;

import com.BillX.Exception.UserException;
import com.BillX.Mapper.StoreMapper;
import com.BillX.Model.Store;
import com.BillX.Model.User;
import com.BillX.Payload.dto.StoreDto;
import com.BillX.Payload.response.ApiResponse;
import com.BillX.Service.StoreService;
import com.BillX.Service.UserService;
import com.BillX.domain.StoreStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store")
public class StoreController {
    private final StoreService storeService;
    private final UserService userService;

    @PostMapping()
    public ResponseEntity<StoreDto> createStore(@RequestBody StoreDto storeDto, @RequestHeader ("Authorization")String jwt)
            throws UserException {
        User user = userService.getUserFromJwt(jwt);
        return ResponseEntity.ok(storeService.createStore(storeDto,user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreDto> getStoreById(@PathVariable Long id,@RequestHeader("Authorization") String jwt)
            throws Exception {
        User user = userService.getUserFromJwt(jwt);
        return ResponseEntity.ok(storeService.getStoreById(id));
    }

    @GetMapping()
    public ResponseEntity<List<StoreDto>> getAllStores(@RequestHeader("Authorization") String jwt)
            throws Exception {
        return ResponseEntity.ok(storeService.getAllStores());
    }

    @GetMapping("/admin")
    public ResponseEntity<StoreDto> getStoreByAdmin(@RequestHeader("Authorization") String jwt)
            throws Exception {
        return ResponseEntity.ok(StoreMapper.toDto(storeService.getStoreByAdmin()));
    }

    @GetMapping("/employee")
    public ResponseEntity<StoreDto> getStoreByEmployee(@RequestHeader("Authorization") String jwt)
            throws Exception {
        return ResponseEntity.ok(StoreMapper.toDto((Store) storeService.getStoreByEmployee()));
    }

    @PutMapping("/{id}")
    private ResponseEntity<StoreDto> updateStore(@PathVariable Long id,@RequestBody StoreDto storeDto) throws UserException {
        return ResponseEntity.ok(storeService.updateStore(id,storeDto));
    }

    @PutMapping("/{id}/moderate")
    private ResponseEntity<StoreDto> moderateStore(@PathVariable Long id, @RequestParam StoreStatus storeStatus)
            throws Exception {
        return ResponseEntity.ok(storeService.moderateStore(id,storeStatus));
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<ApiResponse> deleteStore(@PathVariable Long id) throws UserException {
        storeService.deleteStore(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Deleted Successfully!!!");
        return ResponseEntity.ok(apiResponse);
    }



}
