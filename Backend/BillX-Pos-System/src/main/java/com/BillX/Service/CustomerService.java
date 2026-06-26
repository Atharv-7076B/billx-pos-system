package com.BillX.Service;

import com.BillX.Payload.dto.CustomerDto;
import com.BillX.Payload.dto.OrderDto;

import java.util.List;

public interface CustomerService {
    CustomerDto createCustomer(CustomerDto customerDto) throws Exception;
    CustomerDto updateCustomer(Long id, CustomerDto customerDto) throws Exception;
    void deleteCustomer(Long id) throws Exception;
    CustomerDto getCustomerById(Long id) throws Exception;
    List<CustomerDto> getCustomersByStore(Long storeId);
    List<CustomerDto> searchCustomers(Long storeId, String query);
    List<OrderDto> getCustomerPurchaseHistory(Long customerId) throws Exception;
}
