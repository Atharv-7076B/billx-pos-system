package com.BillX.Repository;

import com.BillX.Model.Inventory;
import com.BillX.Payload.dto.InventoryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {
    Optional<Inventory> findByProductIdAndBranchId(Long productId, Long branchId);
    List<Inventory> findByBranchId(Long branchId);

}
