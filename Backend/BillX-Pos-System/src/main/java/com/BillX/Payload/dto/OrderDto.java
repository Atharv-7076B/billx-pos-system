package com.BillX.Payload.dto;

import com.BillX.domain.OrderStatus;
import com.BillX.domain.PaymentMethod;
import com.BillX.domain.PaymentStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private Long id;
    private String orderNumber;
    private CustomerDto customer;
    private Long customerId;
    private Long branchId;
    private String branchName;
    private Long storeId;
    private Long cashierId;
    private String cashierName;
    private List<OrderItemDto> items;
    private double subtotal;
    private double discountAmount;
    private double taxPercent;
    private double taxAmount;
    private double total;
    private String notes;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private OrderStatus orderStatus;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
