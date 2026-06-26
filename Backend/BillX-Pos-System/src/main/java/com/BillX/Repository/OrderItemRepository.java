package com.BillX.Repository;

import com.BillX.Model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);

    @Query("""
        SELECT oi.product.id, oi.product.name, SUM(oi.quantity) as totalQty, SUM(oi.total) as totalRevenue
        FROM OrderItem oi
        JOIN oi.order o
        WHERE o.store.id = :storeId AND o.createdAt BETWEEN :start AND :end
        GROUP BY oi.product.id, oi.product.name
        ORDER BY totalQty DESC
    """)
    List<Object[]> findTopProductsByStoreAndDateRange(
        @Param("storeId") Long storeId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
}
