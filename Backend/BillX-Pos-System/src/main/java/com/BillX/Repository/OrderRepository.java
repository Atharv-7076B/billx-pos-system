package com.BillX.Repository;

import com.BillX.Model.SaleOrder;
import com.BillX.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<SaleOrder, Long> {
    Optional<SaleOrder> findByOrderNumber(String orderNumber);
    List<SaleOrder> findByStoreId(Long storeId);
    Page<SaleOrder> findByStoreId(Long storeId, Pageable pageable);
    List<SaleOrder> findByCustomerId(Long customerId);
    List<SaleOrder> findByBranchId(Long branchId);

    @Query("SELECT o FROM SaleOrder o WHERE o.store.id = :storeId AND o.createdAt BETWEEN :start AND :end")
    List<SaleOrder> findByStoreIdAndDateRange(
        @Param("storeId") Long storeId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM SaleOrder o WHERE o.store.id = :storeId AND o.orderStatus = 'COMPLETED' AND o.createdAt BETWEEN :start AND :end")
    double sumTotalByStoreAndDateRange(
        @Param("storeId") Long storeId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    @Query("SELECT COUNT(o) FROM SaleOrder o WHERE o.store.id = :storeId AND o.createdAt BETWEEN :start AND :end")
    long countByStoreAndDateRange(
        @Param("storeId") Long storeId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    long countByStoreId(Long storeId);

    @Query("""
        SELECT o FROM SaleOrder o
        WHERE o.store.id = :storeId
        AND (LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :query, '%')))
    """)
    List<SaleOrder> searchByKeyword(@Param("storeId") Long storeId, @Param("query") String query);

    List<SaleOrder> findTop10ByStoreIdOrderByCreatedAtDesc(Long storeId);
}
