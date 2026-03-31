package com.BillX.Service.impl;

import com.BillX.Mapper.UserMapper;
import com.BillX.Model.Branch;
import com.BillX.Model.Store;
import com.BillX.Model.User;
import com.BillX.Payload.dto.UserDto;
import com.BillX.Repository.BranchRepository;
import com.BillX.Repository.StoreRepository;
import com.BillX.Repository.UserRepository;
import com.BillX.Service.EmployeeService;
import com.BillX.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.exec.ExecutionException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final BranchRepository branchRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto createStoreEmployee(UserDto employee, Long storeId) throws Exception {
        Store store = storeRepository.findById(storeId).orElseThrow(
                ()-> new Exception("Store not found")
        );
        Branch branch = null;
        if(employee.getRole()==UserRole.ROLE_BRANCH_MANAGER){
            if(employee.getBranchId()==null){
                throw new Exception("Branch Id is required to create branch Manager");
            }
            branch = branchRepository.findById(employee.getBranchId()).orElseThrow(
                    ()->new Exception("Branch Not Found")
            );
        }
        User user = userMapper.toEntity(employee);
        user.setStore(store);
        user.setBranch(branch);
        user.setPassword(passwordEncoder.encode(employee.getPassword()));
        User savedEmployee = userRepository.save(user);
        if(employee.getRole()==UserRole.ROLE_BRANCH_MANAGER && branch!=null){
            branch.setManager(savedEmployee);
            branchRepository.save(branch);
        }
        return userMapper.toDTO(savedEmployee);
    }

    @Override
    public UserDto createBranchEmployee(UserDto employee, Long branchId) throws Exception {
        Branch branch = branchRepository.findById(employee.getBranchId()).orElseThrow(
                ()->new Exception("Branch Not Found")
        );
        if(employee.getRole()!=UserRole.ROLE_BRANCH_CASHIER ||
        employee.getRole()!=UserRole.ROLE_BRANCH_MANAGER){
            User user = userMapper.toEntity(employee);
            user.setBranch(branch);
            user.setPassword(passwordEncoder.encode(employee.getPassword()));
            return UserMapper.toDTO(userRepository.save(user));
        }
        throw new Exception("Branch Role Not supported");
    }

    @Override
    public User updateEmployee(Long employeeId, UserDto employeeDetails) throws Exception {
        User existingEmployee = userRepository.findById(employeeId).orElseThrow(
                ()->new ExecutionException("Employee Not Exists")
        );
        Branch branch = branchRepository.findById(employeeDetails.getBranchId()).orElseThrow(
                ()->new Exception("Branch Not Found")
        );
        existingEmployee.setBranch(branch);
        existingEmployee.setPassword(employeeDetails.getPassword());
        existingEmployee.setRole(employeeDetails.getRole());
        existingEmployee.setEmail(employeeDetails.getEmail());
        existingEmployee.setFullName(employeeDetails.getFullName());

        return userRepository.save(existingEmployee);
    }

    @Override
    public void deleteEmployee(Long employeeId) throws Exception {
        User employee = userRepository.findById(employeeId).orElseThrow(
                ()-> new Exception("Employee Not Exists")
        );
        userRepository.delete(employee);
    }

    @Override
    public List<User> findStoreEmployee(Long storeId, UserRole role) throws Exception {
        Store store = storeRepository.findById(storeId).orElseThrow(
                ()->new Exception("Store Not found")
        );
        return userRepository.findByStore(store);
    }

    @Override
    public List<User> findBranchEmployee(Long branchId, UserRole role) throws Exception {
        Branch branch = branchRepository.findById(branchId).orElseThrow(
                ()->new Exception("Branch Not Found")
        );
        List<User> employees = userRepository.findByBranch(branch)
                .stream().filter(
                        user -> role == null || user.getRole() == role
                ).collect(Collectors.toList());
        return employees;
    }
}
