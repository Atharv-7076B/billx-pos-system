package com.BillX.Service.impl;

import com.BillX.Mapper.InventoryMapper;
import com.BillX.Model.Branch;
import com.BillX.Model.Inventory;
import com.BillX.Model.Product;
import com.BillX.Payload.dto.InventoryDto;
import com.BillX.Repository.BranchRepository;
import com.BillX.Repository.InventoryRepository;
import com.BillX.Repository.ProductRepository;
import com.BillX.Service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    public InventoryDto createInventory(InventoryDto inventoryDto) throws Exception {
        Branch branch = branchRepository.findById(inventoryDto.getBranchId()).orElseThrow(
                ()->new Exception("Branch Not exists")
        );
        Product product = productRepository.findById(inventoryDto.getProductId()).orElseThrow(
                ()->new Exception("Product Not exists")
        );
        Inventory inventory = InventoryMapper.toEntity(inventoryDto,branch,product);
        Inventory savedInventory = inventoryRepository.save(inventory);
        return inventoryMapper.toDto(savedInventory);
    }

    @Override
    public InventoryDto updateInventory(Long id,InventoryDto inventoryDto) throws Exception {
        Inventory inventory = inventoryRepository.findById(id).orElseThrow(
                ()-> new Exception("Inventory Not exists")
        );
        inventory.setQuantity(inventoryDto.getQuantity());
        Inventory updatedInventory = inventoryRepository.save(inventory);
        return inventoryMapper.toDto(updatedInventory);
    }

    @Override
    public void deleteInventory(Long id) throws Exception {
        Inventory inventory = inventoryRepository.findById(id).orElseThrow(
                ()-> new Exception("Inventory Not exists")
        );
        inventoryRepository.delete(inventory);
    }

    @Override
    public InventoryDto getInventoryById(Long id) throws Exception {
        Inventory inventory = inventoryRepository.findById(id).orElseThrow(
                ()-> new Exception("Inventory Not exists")
        );
        return inventoryMapper.toDto(inventory);
    }

//    @Override
//    public InventoryDto getInventoryByProductIdAndBranchId(Long productId, Long branchId) {
//        Inventory inventory = inventoryRepository.findByProductIdAndBranchId(productId,branchId);
//        return InventoryMapper.toDto(inventory);
//    }

    @Override
    public InventoryDto getInventoryByProductIdAndBranchId(Long productId, Long branchId) {
        Inventory inventory = inventoryRepository
                .findByProductIdAndBranchId(productId, branchId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        return inventoryMapper.toDto(inventory);
    }

    @Override
    public List<InventoryDto> getAllInventoryByBranchId(Long branchId) {
        List<Inventory> inventories = inventoryRepository.findByBranchId(branchId);
        return inventories.stream().map(InventoryMapper::toDto).collect(Collectors.toList());
    }
}
