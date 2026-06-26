package com.BillX.Service;

import com.BillX.Model.User;
import com.BillX.Payload.dto.OrderDto;
import com.BillX.Payload.request.CreateOrderRequest;
import com.BillX.domain.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderDto createOrder(CreateOrderRequest request, User cashier) throws Exception;
    OrderDto getOrderById(Long id) throws Exception;
    OrderDto getOrderByNumber(String orderNumber) throws Exception;
    List<OrderDto> getOrdersByStore(Long storeId);
    List<OrderDto> getOrdersByCustomer(Long customerId);
    List<OrderDto> getOrdersByBranch(Long branchId);
    OrderDto updateOrderStatus(Long id, OrderStatus status) throws Exception;
    OrderDto cancelOrder(Long id) throws Exception;
    List<OrderDto> searchOrders(Long storeId, String query);
    List<OrderDto> getRecentOrders(Long storeId);
}
