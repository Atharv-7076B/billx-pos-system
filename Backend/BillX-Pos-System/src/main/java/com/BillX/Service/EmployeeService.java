package com.BillX.Service;

import com.BillX.Model.User;
import com.BillX.Payload.dto.UserDto;
import com.BillX.domain.UserRole;

import java.util.List;

public interface EmployeeService {
    UserDto createStoreEmployee(UserDto employee,Long storeId)throws Exception;
    UserDto createBranchEmployee(UserDto employee,Long branchId) throws  Exception;
    User updateEmployee(UserDto employee,User employeeDetails);
    void deleteEmployee(Long employeeId);
    List<User> findStoreEmployee(Long storeId, UserRole role);
    List<User> findBranchEmployee(Long branchId, UserRole role);
}
