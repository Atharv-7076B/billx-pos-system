package com.BillX.Repository;

import com.BillX.Model.Product;
import com.BillX.Payload.dto.ProductDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findByStoreId(Long storeId);
    @Query
    List<Product> searchByKeyword(@Param("storeId")Long storeId,@Param("query") String keyword);

}
