package com.BillX.Mapper;

import com.BillX.Model.OrderItem;
import com.BillX.Model.SaleOrder;
import com.BillX.Payload.dto.CustomerDto;
import com.BillX.Payload.dto.OrderDto;
import com.BillX.Payload.dto.OrderItemDto;
import java.util.stream.Collectors;


public class OrderMapper {

    public static OrderDto toDto(SaleOrder order) {
        if (order == null) return null;
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setSubtotal(order.getSubtotal());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setTaxPercent(order.getTaxPercent());
        dto.setTaxAmount(order.getTaxAmount());
        dto.setTotal(order.getTotal());
        dto.setNotes(order.getNotes());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setRazorpayOrderId(order.getRazorpayOrderId());
        dto.setRazorpayPaymentId(order.getRazorpayPaymentId());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        if (order.getStore() != null) dto.setStoreId(order.getStore().getId());
        if (order.getBranch() != null) {
            dto.setBranchId(order.getBranch().getId());
            dto.setBranchName(order.getBranch().getName());
        }
        if (order.getCashier() != null) {
            dto.setCashierId(order.getCashier().getId());
            dto.setCashierName(order.getCashier().getFullName());
        }
        if (order.getCustomer() != null) {
            dto.setCustomerId(order.getCustomer().getId());
            CustomerDto customerDto = new CustomerDto();
            customerDto.setId(order.getCustomer().getId());
            customerDto.setName(order.getCustomer().getName());
            customerDto.setEmail(order.getCustomer().getEmail());
            customerDto.setPhone(order.getCustomer().getPhone());
            dto.setCustomer(customerDto);
        }
        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream().map(OrderMapper::toItemDto).collect(Collectors.toList()));
        }
        return dto;
    }

    public static OrderItemDto toItemDto(OrderItem item) {
        if (item == null) return null;
        OrderItemDto dto = new OrderItemDto();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct() != null ? item.getProduct().getId() : null);
        dto.setProductName(item.getProduct() != null ? item.getProduct().getName() : null);
        dto.setProductSku(item.getProduct() != null ? item.getProduct().getSku() : null);
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setDiscountAmount(item.getDiscountAmount());
        dto.setTotal(item.getTotal());
        return dto;
    }
}
