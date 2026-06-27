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
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

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
                // create a single store with realistic demo data if none exists
                User storeAdmin = createUser("Amit Patel", "admin@billx.dev", "Admin@123", UserRole.ROLE_STORE_MANAGER,
                                null,
                                null);

                Store store = createStore("BillX Retail", "Retail and grocery store", "RETAIL", storeAdmin,
                                StoreContact.builder()
                                                .address("12 Market Lane, Mumbai")
                                                .phoneNo("+91-9876543210")
                                                .email("contact@billx.dev")
                                                .build());

                storeAdmin.setStore(store);
                userRepository.save(storeAdmin);

                // Create 3 branches (Central + 2 others)
                Branch centralBranch = createBranch(store, "Central Branch", "101 Main Street, Mumbai",
                                "+91-9922334455",
                                "central@billx.dev");
                User centralManager = createUser("Priya Shah", "manager@billx.dev", "Manager@123",
                                UserRole.ROLE_BRANCH_MANAGER,
                                store, centralBranch);
                centralBranch.setManager(centralManager);
                branchRepository.save(centralBranch);

                Branch northBranch = createBranch(store, "North Branch", "22 North Road, Mumbai", "+91-9911002233",
                                "north@billx.dev");
                User northManager = createUser("Kumar Singh", "nmgr@billx.dev", "Manager@123",
                                UserRole.ROLE_BRANCH_MANAGER,
                                store, northBranch);
                northBranch.setManager(northManager);
                branchRepository.save(northBranch);

                Branch southBranch = createBranch(store, "South Branch", "9 South Avenue, Mumbai", "+91-9900112233",
                                "south@billx.dev");
                User southManager = createUser("Sunita Rao", "smgr@billx.dev", "Manager@123",
                                UserRole.ROLE_BRANCH_MANAGER,
                                store, southBranch);
                southBranch.setManager(southManager);
                branchRepository.save(southBranch);

                // Create a cashier for each branch
                User centralCashier = createUser("Rohan Mehta", "cashier.central@billx.dev", "Cashier@123",
                                UserRole.ROLE_BRANCH_CASHIER,
                                store, centralBranch);
                User northCashier = createUser("Asha Patel", "cashier.north@billx.dev", "Cashier@123",
                                UserRole.ROLE_BRANCH_CASHIER,
                                store, northBranch);
                User southCashier = createUser("Vikram Joshi", "cashier.south@billx.dev", "Cashier@123",
                                UserRole.ROLE_BRANCH_CASHIER,
                                store, southBranch);

                // Create 10 categories
                List<Category> categories = new ArrayList<>();
                IntStream.range(0, 10).forEach(i -> categories.add(createCategory("Category " + (i + 1), store)));

                // Create 100 products across categories
                List<Product> products = new ArrayList<>();
                for (int i = 1; i <= 100; i++) {
                        Category cat = categories.get(ThreadLocalRandom.current().nextInt(categories.size()));
                        String sku = String.format("P-%04d", i);
                        double mrp = 50 + ThreadLocalRandom.current().nextDouble(0, 950);
                        double selling = mrp * (0.7 + ThreadLocalRandom.current().nextDouble(0, 0.25));
                        products.add(createProduct("Product " + i, sku, "Brand" + ((i % 10) + 1), cat, store,
                                        Math.round(mrp * 100.0) / 100.0,
                                        Math.round(selling * 100.0) / 100.0, "Sample product " + i));
                }

                // Populate inventory for every branch and product
                List<Branch> branches = Arrays.asList(centralBranch, northBranch, southBranch);
                for (Product p : products) {
                        for (Branch b : branches) {
                                int qty = ThreadLocalRandom.current().nextInt(20, 200);
                                createInventory(p, b, qty);
                        }
                }

                // Create 100 customers
                List<Customer> customers = new ArrayList<>();
                for (int i = 1; i <= 100; i++) {
                        customers.add(createCustomer("Customer " + i, "customer" + i + "@example.com",
                                        "+91-9" + (100000000 + i),
                                        i + " Example Street, Mumbai", store));
                }

                // Create 50 orders with up to 6 items each (total ~300 items)
                int orderCount = 50;
                int orderNumBase = 2026000;
                for (int o = 1; o <= orderCount; o++) {
                        Branch branch = branches.get(ThreadLocalRandom.current().nextInt(branches.size()));
                        User cashier = switch (branch.getName()) {
                                case "North Branch" -> northCashier;
                                case "South Branch" -> southCashier;
                                default -> centralCashier;
                        };

                        Customer cust = customers.get(ThreadLocalRandom.current().nextInt(customers.size()));

                        int itemsCount = ThreadLocalRandom.current().nextInt(1, 7);
                        List<OrderItemRequest> items = new ArrayList<>();
                        for (int k = 0; k < itemsCount; k++) {
                                Product p = products.get(ThreadLocalRandom.current().nextInt(products.size()));
                                int qty = ThreadLocalRandom.current().nextInt(1, 6);
                                items.add(OrderItemRequest.of(p, qty, p.getSellingPrice(), 0));
                        }

                        createOrder("ORD-" + (orderNumBase + o), store, branch, cashier, cust, items,
                                        5.0, 0.0, PaymentMethod.CASH, PaymentStatus.PAID, OrderStatus.COMPLETED,
                                        "Auto-generated order");
                }
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
                                .workingDays(Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
                                                "Saturday"))
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
