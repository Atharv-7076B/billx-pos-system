package com.BillX.controller;

import com.BillX.Mapper.UserMapper;
import com.BillX.Model.User;
import com.BillX.Payload.dto.UserDto;
import com.BillX.Payload.response.StandardResponse;
import com.BillX.Service.EmployeeService;
import com.BillX.domain.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
@Tag(name = "Employees", description = "Employee management APIs")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/store/{storeId}")
    @Operation(summary = "Create a store-level employee")
    public ResponseEntity<StandardResponse<UserDto>> createStoreEmployee(
            @PathVariable Long storeId,
            @RequestBody UserDto employee) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.success("Employee created",
                        employeeService.createStoreEmployee(employee, storeId)));
    }

    @PostMapping("/branch/{branchId}")
    @Operation(summary = "Create a branch-level employee")
    public ResponseEntity<StandardResponse<UserDto>> createBranchEmployee(
            @PathVariable Long branchId,
            @RequestBody UserDto employee) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.success("Employee created",
                        employeeService.createBranchEmployee(employee, branchId)));
    }

    @PutMapping("/{employeeId}")
    @Operation(summary = "Update employee")
    public ResponseEntity<StandardResponse<UserDto>> updateEmployee(
            @PathVariable Long employeeId,
            @RequestBody UserDto employee) throws Exception {
        User updated = employeeService.updateEmployee(employeeId, employee);
        return ResponseEntity.ok(StandardResponse.success("Employee updated", UserMapper.toDTO(updated)));
    }

    @DeleteMapping("/{employeeId}")
    @Operation(summary = "Delete employee")
    public ResponseEntity<StandardResponse<Void>> deleteEmployee(@PathVariable Long employeeId) throws Exception {
        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.ok(StandardResponse.success("Employee deleted", null));
    }

    @GetMapping("/store/{storeId}")
    @Operation(summary = "Get all employees by store")
    public ResponseEntity<StandardResponse<List<UserDto>>> getByStore(
            @PathVariable Long storeId,
            @RequestParam(required = false) UserRole role) throws Exception {
        List<User> employees = employeeService.findStoreEmployee(storeId, role);
        List<UserDto> dtos = employees.stream().map(UserMapper::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(StandardResponse.success(dtos));
    }

    @GetMapping("/branch/{branchId}")
    @Operation(summary = "Get all employees by branch")
    public ResponseEntity<StandardResponse<List<UserDto>>> getByBranch(
            @PathVariable Long branchId,
            @RequestParam(required = false) UserRole role) throws Exception {
        List<User> employees = employeeService.findBranchEmployee(branchId, role);
        List<UserDto> dtos = employees.stream().map(UserMapper::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(StandardResponse.success(dtos));
    }
}