package com.BillX.controller;

import com.BillX.Exception.UserException;
import com.BillX.Payload.dto.StoreDto;
import com.BillX.Payload.response.ApiResponse;
import com.BillX.Service.StoreService;
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

    @PostMapping("/create")
    public ResponseEntity<StoreDto> createStore(@RequestBody StoreDto storeDto)
            throws UserException {

        // ✅ NO NULL USER ANYMORE
        return ResponseEntity.ok(storeService.createStore(storeDto));
    }

    @GetMapping
    public ResponseEntity<List<StoreDto>> getAllStores() {
        return ResponseEntity.ok(storeService.getAllStores());
    }

    @GetMapping("/admin")
    public ResponseEntity<StoreDto> getStoreByAdmin()
            throws UserException {
        return ResponseEntity.ok(storeService.getStoreByAdmin());
    }

    @GetMapping("/employee")
    public ResponseEntity<StoreDto> getStoreByEmployee()
            throws UserException {
        return ResponseEntity.ok(storeService.getStoreByEmployee());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreDto> getStoreById(@PathVariable Long id)
            throws Exception {
        return ResponseEntity.ok(storeService.getStoreById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoreDto> updateStore(
            @PathVariable Long id,
            @RequestBody StoreDto storeDto) throws UserException {
        return ResponseEntity.ok(storeService.updateStore(id, storeDto));
    }

    @PutMapping("/{id}/moderate")
    public ResponseEntity<StoreDto> moderateStore(
            @PathVariable Long id,
            @RequestParam StoreStatus storeStatus) throws Exception {
        return ResponseEntity.ok(storeService.moderateStore(id, storeStatus));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteStore(@PathVariable Long id)
            throws UserException {
        storeService.deleteStore(id);
        ApiResponse response = new ApiResponse();
        response.setMessage("Deleted Successfully!!!");
        return ResponseEntity.ok(response);
    }
}
