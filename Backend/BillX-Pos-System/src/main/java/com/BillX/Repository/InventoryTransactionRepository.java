package com.BillX.Repository;

import com.BillX.Model.InventoryTransaction;
import com.BillX.domain.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    List<InventoryTransaction> findByInventoryIdOrderByCreatedAtDesc(Long inventoryId);
    List<InventoryTransaction> findByInventoryBranchIdOrderByCreatedAtDesc(Long branchId);
    List<InventoryTransaction> findByTransactionTypeAndInventoryBranchId(TransactionType type, Long branchId);
}
