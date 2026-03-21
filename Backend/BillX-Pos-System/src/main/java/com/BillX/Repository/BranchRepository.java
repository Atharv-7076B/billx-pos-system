package com.BillX.Repository;

import com.BillX.Model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BranchRepository extends JpaRepository<Branch,Long> {
    List<Branch> findByStoreId(Long storeID);
}
