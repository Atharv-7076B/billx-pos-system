package com.BillX.Repository;

import com.BillX.Model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByStoreId(Long storeId);
    Optional<Customer> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("""
        SELECT c FROM Customer c
        WHERE c.store.id = :storeId
        AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))
          OR LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%'))
          OR c.phone LIKE CONCAT('%', :query, '%'))
    """)
    List<Customer> searchByKeyword(@Param("storeId") Long storeId, @Param("query") String query);

    Page<Customer> findByStoreId(Long storeId, Pageable pageable);

    long countByStoreId(Long storeId);
}
