package com.BillX.Service.impl;

import com.BillX.Exception.UserException;
import com.BillX.Mapper.OrderMapper;
import com.BillX.Model.*;
import com.BillX.Payload.dto.OrderDto;
import com.BillX.Payload.dto.OrderItemDto;
import com.BillX.Payload.request.CreateOrderRequest;
import com.BillX.Repository.*;
import com.BillX.Service.OrderService;
import com.BillX.domain.OrderStatus;
import com.BillX.domain.PaymentStatus;
import com.BillX.domain.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final BranchRepository branchRepository;
    private final CustomerRepository customerRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;

    private static final AtomicLong counter = new AtomicLong(System.currentTimeMillis() % 100000);

    @Override
    public OrderDto createOrder(CreateOrderRequest request, User cashier) throws Exception {
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new UserException("Store not found"));

        Branch branch = null;
        if (request.getBranchId() != null) {
            branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new UserException("Branch not found"));
        }

        Customer customer = null;
        if (request.getCustomerId() != null) {
            customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new UserException("Customer not found"));
        }

        SaleOrder order = SaleOrder.builder()
                .orderNumber(generateOrderNumber())
                .store(store)
                .branch(branch)
                .customer(customer)
                .cashier(cashier)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.PAID)
                .orderStatus(OrderStatus.COMPLETED)
                .taxPercent(request.getTaxPercent())
                .discountAmount(request.getDiscountAmount())
                .notes(request.getNotes())
                .build();

        SaleOrder savedOrder = orderRepository.save(order);

        double subtotal = 0;
        for (OrderItemDto itemDto : request.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new UserException("Product not found: " + itemDto.getProductId()));

            double unitPrice = itemDto.getUnitPrice() > 0 ? itemDto.getUnitPrice() : product.getSellingPrice();
            double itemTotal = (unitPrice * itemDto.getQuantity()) - itemDto.getDiscountAmount();

            OrderItem item = OrderItem.builder()
                    .order(savedOrder)
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .unitPrice(unitPrice)
                    .discountAmount(itemDto.getDiscountAmount())
                    .total(itemTotal)
                    .build();

            orderItemRepository.save(item);
            savedOrder.getItems().add(item);
            subtotal += itemTotal;

            if (request.isDeductInventory() && branch != null) {
                deductInventory(product, branch, itemDto.getQuantity(), savedOrder, cashier);
            }
        }

        double taxAmount = subtotal * (request.getTaxPercent() / 100.0);
        double total = subtotal - request.getDiscountAmount() + taxAmount;

        savedOrder.setSubtotal(subtotal);
        savedOrder.setTaxAmount(taxAmount);
        savedOrder.setTotal(total);
        orderRepository.save(savedOrder);

        if (customer != null) {
            customer.setTotalOrders(customer.getTotalOrders() + 1);
            customer.setTotalPurchases(customer.getTotalPurchases() + total);
            customerRepository.save(customer);
        }

        log.info("Order created: {} for store {}", savedOrder.getOrderNumber(), store.getId());
        return OrderMapper.toDto(savedOrder);
    }

    private void deductInventory(Product product, Branch branch, int quantity, SaleOrder order, User user) {
        inventoryRepository.findByProductIdAndBranchId(product.getId(), branch.getId())
                .ifPresent(inventory -> {
                    int prev = inventory.getQuantity();
                    int newQty = Math.max(0, prev - quantity);
                    inventory.setQuantity(newQty);
                    inventory.setLastUpdate(LocalDateTime.now());
                    inventoryRepository.save(inventory);

                    InventoryTransaction tx = InventoryTransaction.builder()
                            .inventory(inventory)
                            .transactionType(TransactionType.SALE)
                            .quantity(quantity)
                            .previousQuantity(prev)
                            .newQuantity(newQty)
                            .reason("Sale - Order #" + order.getOrderNumber())
                            .performedBy(user)
                            .order(order)
                            .build();
                    inventoryTransactionRepository.save(tx);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long id) throws Exception {
        return OrderMapper.toDto(orderRepository.findById(id)
                .orElseThrow(() -> new UserException("Order not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderByNumber(String orderNumber) throws Exception {
        return OrderMapper.toDto(orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new UserException("Order not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByStore(Long storeId) {
        return orderRepository.findByStoreId(storeId)
                .stream().map(OrderMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId)
                .stream().map(OrderMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByBranch(Long branchId) {
        return orderRepository.findByBranchId(branchId)
                .stream().map(OrderMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public OrderDto updateOrderStatus(Long id, OrderStatus status) throws Exception {
        SaleOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new UserException("Order not found"));
        order.setOrderStatus(status);
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderDto cancelOrder(Long id) throws Exception {
        SaleOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new UserException("Order not found"));
        if (order.getOrderStatus() == OrderStatus.COMPLETED) {
            order.setOrderStatus(OrderStatus.CANCELLED);
            order.setPaymentStatus(PaymentStatus.REFUNDED);
            return OrderMapper.toDto(orderRepository.save(order));
        }
        throw new UserException("Only completed orders can be cancelled");
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> searchOrders(Long storeId, String query) {
        return orderRepository.searchByKeyword(storeId, query)
                .stream().map(OrderMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getRecentOrders(Long storeId) {
        return orderRepository.findTop10ByStoreIdOrderByCreatedAtDesc(storeId)
                .stream().map(OrderMapper::toDto).collect(Collectors.toList());
    }

    private String generateOrderNumber() {
        return "ORD-" + DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now())
                + "-" + String.format("%05d", counter.incrementAndGet() % 100000);
    }
}
