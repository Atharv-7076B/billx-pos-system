package com.BillX.Service.impl;

import com.BillX.Exception.UserException;
import com.BillX.Mapper.OrderMapper;
import com.BillX.Model.Customer;
import com.BillX.Model.Store;
import com.BillX.Payload.dto.CustomerDto;
import com.BillX.Payload.dto.OrderDto;
import com.BillX.Repository.CustomerRepository;
import com.BillX.Repository.OrderRepository;
import com.BillX.Repository.StoreRepository;
import com.BillX.Service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;

    @Override
    public CustomerDto createCustomer(CustomerDto dto) throws Exception {
        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new UserException("Store not found"));

        if (dto.getEmail() != null && !dto.getEmail().isBlank()
                && customerRepository.existsByEmail(dto.getEmail())) {
            throw new UserException("Customer with this email already exists");
        }

        Customer customer = Customer.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .store(store)
                .build();

        return toDto(customerRepository.save(customer));
    }

    @Override
    public CustomerDto updateCustomer(Long id, CustomerDto dto) throws Exception {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new UserException("Customer not found"));

        customer.setName(dto.getName());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        if (dto.getEmail() != null && !dto.getEmail().equals(customer.getEmail())) {
            if (customerRepository.existsByEmail(dto.getEmail())) {
                throw new UserException("Email already used by another customer");
            }
            customer.setEmail(dto.getEmail());
        }

        return toDto(customerRepository.save(customer));
    }

    @Override
    public void deleteCustomer(Long id) throws Exception {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new UserException("Customer not found"));
        customerRepository.delete(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDto getCustomerById(Long id) throws Exception {
        return toDto(customerRepository.findById(id)
                .orElseThrow(() -> new UserException("Customer not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDto> getCustomersByStore(Long storeId) {
        return customerRepository.findByStoreId(storeId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDto> searchCustomers(Long storeId, String query) {
        return customerRepository.searchByKeyword(storeId, query)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getCustomerPurchaseHistory(Long customerId) throws Exception {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new UserException("Customer not found"));
        return orderRepository.findByCustomerId(customerId)
                .stream().map(OrderMapper::toDto).collect(Collectors.toList());
    }

    private CustomerDto toDto(Customer c) {
        CustomerDto dto = new CustomerDto();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setEmail(c.getEmail());
        dto.setPhone(c.getPhone());
        dto.setAddress(c.getAddress());
        dto.setStoreId(c.getStore() != null ? c.getStore().getId() : null);
        dto.setTotalPurchases(c.getTotalPurchases());
        dto.setTotalOrders(c.getTotalOrders());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setUpdatedAt(c.getUpdatedAt());
        return dto;
    }
}
