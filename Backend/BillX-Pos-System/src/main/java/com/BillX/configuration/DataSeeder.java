package com.BillX.configuration;

import com.BillX.Model.*;
import com.BillX.Repository.*;
import com.BillX.domain.OrderStatus;
import com.BillX.domain.PaymentMethod;
import com.BillX.domain.PaymentStatus;
import com.BillX.domain.TransactionType;
import com.BillX.domain.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "seed", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final BranchRepository branchRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (storeRepository.count() > 0 || userRepository.count() > 0) {
            log.info("DataSeeder skipped because database already contains data.");
            return;
        }

        log.info("Seeding sample BillX POS data...");
        seedInitialData();
        log.info("Sample BillX POS data seeding completed.");
    }

    private void seedInitialData() {
        User storeAdmin = createUser("Amit Patel", "admin@billx.dev", "Admin@123", UserRole.ROLE_STORE_MANAGER, null,
                null);

        Store store = createStore("BillX Retail", "Retail and grocery store", "Grocery", storeAdmin,
                StoreContact.builder()
                        .address("12 Market Lane, Mumbai")
                        .phoneNo("+91-9876543210")
                        .email("contact@billx.dev")
                        .build());

        storeAdmin.setStore(store);
        userRepository.save(storeAdmin);

        Branch centralBranch = createBranch(store, "Central Branch", "101 Main Street, Mumbai", "+91-9922334455",
                "central@billx.dev");
        User branchManager = createUser("Priya Shah", "manager@billx.dev", "Manager@123", UserRole.ROLE_BRANCH_MANAGER,
                store, centralBranch);
        centralBranch.setManager(branchManager);
        branchRepository.save(centralBranch);

        User cashier = createUser("Rohan Mehta", "cashier@billx.dev", "Cashier@123", UserRole.ROLE_BRANCH_CASHIER,
                store, centralBranch);

        List<Category> categories = Arrays.asList(
                createCategory("Beverages", store),
                createCategory("Snacks", store),
                createCategory("Home & Kitchen", store),
                createCategory("Personal Care", store));

        List<Product> products = Arrays.asList(
                createProduct("Mineral Water", "BW-001", "AQUA-FRESH", categories.get(0), store, 25.00, 18.00,
                        "Mineral water 1L bottle"),
                createProduct("Orange Juice", "OJ-002", "FreshSip", categories.get(0), store, 120.00, 95.00,
                        "Fresh squeezed orange juice"),
                createProduct("Potato Chips", "SC-101", "Crunchy", categories.get(1), store, 60.00, 45.00,
                        "Salted potato chips 50g"),
                createProduct("Dish Soap", "HK-210", "Sparkle", categories.get(2), store, 180.00, 145.00,
                        "Lemon dishwashing liquid 500ml"),
                createProduct("Shampoo", "PC-320", "Silky", categories.get(3), store, 250.00, 199.00,
                        "Herbal shampoo 250ml"));

        createInventory(products.get(0), centralBranch, 120);
        createInventory(products.get(1), centralBranch, 80);
        createInventory(products.get(2), centralBranch, 200);
        createInventory(products.get(3), centralBranch, 90);
        createInventory(products.get(4), centralBranch, 75);

        List<Customer> customers = Arrays.asList(
                createCustomer("Neha Sharma", "neha.sharma@example.com", "+91-9988776655", "25 Residency Road, Mumbai",
                        store),
                createCustomer("Rahul Khanna", "rahul.khanna@example.com", "+91-9911223344", "47 Palm Avenue, Mumbai",
                        store),
                createCustomer("Sunita Desai", "sunita.desai@example.com", "+91-9822446688", "12 Lake View, Mumbai",
                        store));

        createOrder("ORD-2026001", store, centralBranch, cashier, customers.get(0),
                Arrays.asList(
                        OrderItemRequest.of(products.get(0), 3, 18.00, 0),
                        OrderItemRequest.of(products.get(2), 2, 45.00, 0)),
                5.0, 10.0, PaymentMethod.CASH, PaymentStatus.PAID, OrderStatus.COMPLETED,
                "In-person purchase");

        createOrder("ORD-2026002", store, centralBranch, cashier, customers.get(1),
                Arrays.asList(
                        OrderItemRequest.of(products.get(1), 1, 95.00, 0),
                        OrderItemRequest.of(products.get(3), 1, 145.00, 10),
                        OrderItemRequest.of(products.get(4), 2, 199.00, 0)),
                12.0, 20.0, PaymentMethod.CARD, PaymentStatus.PAID, OrderStatus.COMPLETED,
                "Card payment at counter");

        createOrder("ORD-2026003", store, centralBranch, cashier, null,
                Arrays.asList(
                        OrderItemRequest.of(products.get(2), 5, 45.00, 5)),
                8.0, 5.0, PaymentMethod.UPI, PaymentStatus.PAID, OrderStatus.COMPLETED,
                "Walk-in sale");
    }

    private User createUser(String fullName, String email, String rawPassword, UserRole role, Store store,
            Branch branch) {
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setStore(store);
        user.setBranch(branch);
        user.setPhoneNumber(branch != null ? branch.getPhone() : "+91-9000000000");
        user.setCreatedAt(java.time.LocalDateTime.now());
        return userRepository.save(user);
    }

    private Store createStore(String brand, String description, String storeType, User storeAdmin,
            StoreContact contact) {
        Store store = new Store();
        store.setBrand(brand);
        store.setDescription(description);
        store.setStoreType(storeType);
        store.setStoreAdmin(storeAdmin);
        store.setStoreContact(contact);
        store.setStatus(com.BillX.domain.StoreStatus.ACTIVE);
        return storeRepository.save(store);
    }

    private Branch createBranch(Store store, String name, String address, String phone, String email) {
        Branch branch = Branch.builder()
                .name(name)
                .address(address)
                .phone(phone)
                .email(email)
                .workingDays(Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"))
                .openTime(java.time.LocalTime.of(9, 30))
                .closeTime(java.time.LocalTime.of(20, 0))
                .store(store)
                .build();
        return branchRepository.save(branch);
    }

    private Category createCategory(String name, Store store) {
        Category category = Category.builder()
                .name(name)
                .store(store)
                .build();
        return categoryRepository.save(category);
    }

    private Product createProduct(String name, String sku, String brand, Category category, Store store,
            double mrp, double sellingPrice, String description) {
        Product product = Product.builder()
                .name(name)
                .sku(sku)
                .brand(brand)
                .category(category)
                .store(store)
                .mrp(mrp)
                .sellingPrice(sellingPrice)
                .description(description)
                .build();
        return productRepository.save(product);
    }

    private Inventory createInventory(Product product, Branch branch, int quantity) {
        Inventory inventory = Inventory.builder()
                .product(product)
                .branch(branch)
                .quantity(quantity)
                .build();
        return inventoryRepository.save(inventory);
    }

    private Customer createCustomer(String name, String email, String phone, String address, Store store) {
        Customer customer = Customer.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .address(address)
                .store(store)
                .totalOrders(0)
                .totalPurchases(0.0)
                .build();
        return customerRepository.save(customer);
    }

    private void createOrder(String orderNumber,
            Store store,
            Branch branch,
            User cashier,
            Customer customer,
            List<OrderItemRequest> itemRequests,
            double taxPercent,
            double discountAmount,
            PaymentMethod paymentMethod,
            PaymentStatus paymentStatus,
            OrderStatus orderStatus,
            String notes) {

        SaleOrder order = SaleOrder.builder()
                .orderNumber(orderNumber)
                .store(store)
                .branch(branch)
                .customer(customer)
                .cashier(cashier)
                .paymentMethod(paymentMethod)
                .paymentStatus(paymentStatus)
                .orderStatus(orderStatus)
                .taxPercent(taxPercent)
                .discountAmount(discountAmount)
                .notes(notes)
                .build();

        order = orderRepository.save(order);

        double subtotal = 0.0;
        List<OrderItem> savedItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : itemRequests) {
            double total = (itemRequest.unitPrice * itemRequest.quantity) - itemRequest.discountAmount;
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(itemRequest.product)
                    .quantity(itemRequest.quantity)
                    .unitPrice(itemRequest.unitPrice)
                    .discountAmount(itemRequest.discountAmount)
                    .total(total)
                    .build();
            OrderItem savedItem = orderItemRepository.save(orderItem);
            savedItems.add(savedItem);
            subtotal += total;
            deductInventory(branch, itemRequest.product, itemRequest.quantity, order, cashier);
        }

        order.setItems(savedItems);
        order.setSubtotal(subtotal);
        order.setTaxAmount(subtotal * (taxPercent / 100.0));
        order.setTotal(subtotal - discountAmount + order.getTaxAmount());
        orderRepository.save(order);

        if (customer != null) {
            customer.setTotalOrders(customer.getTotalOrders() + 1);
            customer.setTotalPurchases(customer.getTotalPurchases() + order.getTotal());
            customerRepository.save(customer);
        }
    }

    private void deductInventory(Branch branch, Product product, int quantity, SaleOrder order, User cashier) {
        inventoryRepository.findByProductIdAndBranchId(product.getId(), branch.getId())
                .ifPresent(inventory -> {
                    int previousQuantity = inventory.getQuantity();
                    int newQuantity = Math.max(0, previousQuantity - quantity);
                    inventory.setQuantity(newQuantity);
                    inventoryRepository.save(inventory);

                    InventoryTransaction transaction = InventoryTransaction.builder()
                            .inventory(inventory)
                            .transactionType(TransactionType.SALE)
                            .quantity(quantity)
                            .previousQuantity(previousQuantity)
                            .newQuantity(newQuantity)
                            .reason("Sale order " + order.getOrderNumber())
                            .performedBy(cashier)
                            .order(order)
                            .build();
                    inventoryTransactionRepository.save(transaction);
                });
    }

    private record OrderItemRequest(Product product, int quantity, double unitPrice, double discountAmount) {
        static OrderItemRequest of(Product product, int quantity, double unitPrice, double discountAmount) {
            return new OrderItemRequest(product, quantity, unitPrice, discountAmount);
        }
    }
}
