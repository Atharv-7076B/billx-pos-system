package com.BillX.Repository;

import com.BillX.Model.Category;
import com.BillX.Payload.dto.StoreDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    StoreDto getByStoreId(Long storeId);

}
