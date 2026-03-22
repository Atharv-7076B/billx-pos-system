package com.BillX.controller;

import com.BillX.Payload.dto.InventoryDto;
import com.BillX.Payload.response.ApiResponse;
import com.BillX.Service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryDto> createInventory(@RequestBody InventoryDto inventoryDto) throws Exception {
        return ResponseEntity.ok(inventoryService.createInventory(inventoryDto));
    }
    @PutMapping("/{id}")
    public ResponseEntity<InventoryDto> updateInventory(@RequestBody InventoryDto inventoryDto,@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(inventoryService.updateInventory(id,inventoryDto));
    }
    @DeleteMapping("/{inventoryId}")
    public ResponseEntity<ApiResponse> deleteInventory(@PathVariable Long inventoryId) throws Exception {
        inventoryService.deleteInventory(inventoryId);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Inventory deleted successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{inventoryId}")
    public ResponseEntity<InventoryDto> getInventoryById(@PathVariable Long inventoryId) throws Exception {
        InventoryDto inventory = inventoryService.getInventoryById(inventoryId);
        return ResponseEntity.ok(inventory);
    }

    @GetMapping("/branch/{branchId}/product/{productId}")
    public ResponseEntity<InventoryDto> getInventoryByBranchIdAndProductId(@PathVariable long branchId
            ,@PathVariable long productId){
        InventoryDto inventory = inventoryService.getInventoryByProductIdAndBranchId(productId,branchId);
        return ResponseEntity.ok(inventory);
    }
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<InventoryDto>> getAllInventoryByBranchId(@PathVariable long branchId){
        List<InventoryDto> inventories = inventoryService.getAllInventoryByBranchId(branchId);
        return ResponseEntity.ok(inventories);
    }

}
