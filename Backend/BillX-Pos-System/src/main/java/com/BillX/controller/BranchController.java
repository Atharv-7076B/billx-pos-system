package com.BillX.controller;

import com.BillX.Exception.UserException;
import com.BillX.Model.Branch;
import com.BillX.Model.User;
import com.BillX.Payload.dto.BranchDto;
import com.BillX.Payload.response.ApiResponse;
import com.BillX.Service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/branches")
public class BranchController {
    private final BranchService branchService;
    @PostMapping
    public ResponseEntity<BranchDto> createBranch(@RequestBody BranchDto branchDto) throws UserException {
        BranchDto createdBranch = branchService.createBranch(branchDto,null);
        return ResponseEntity.ok(createdBranch);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchDto> getBranchById(@PathVariable Long id) throws Exception {
        BranchDto branch = branchService.getByBranchId(id);
        return ResponseEntity.ok(branch);

    }
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<BranchDto>> getAllBranchedByStoreId(@PathVariable Long storeId) throws Exception {
        List<BranchDto> branch = branchService.getAllBranchesByStoreId(storeId);
        return ResponseEntity.ok(branch);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BranchDto> updateBranch(@PathVariable Long id
            , @RequestBody BranchDto branchDto
            , User user) throws Exception {
        BranchDto branch = branchService.updateBranch(id,branchDto);
        return ResponseEntity.ok(branch);
    }
    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteBranch(@PathVariable Long id) throws  Exception{
        branchService.deleteBranch(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Branch Deleted Successfully");
        return ResponseEntity.ok(apiResponse);
    }
}
