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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
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

        }

        return null;
    }

    @Override
    public User updateEmployee(UserDto employee, User employeeDetails) {
        return null;
    }

    @Override
    public void deleteEmployee(Long employeeId) {

    }

    @Override
    public List<User> findStoreEmployee(Long storeId, UserRole role) {
        return List.of();
    }

    @Override
    public List<User> findBranchEmployee(Long branchId, UserRole role) {
        return List.of();
    }
}
