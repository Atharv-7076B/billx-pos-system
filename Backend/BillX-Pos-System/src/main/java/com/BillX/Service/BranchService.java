package com.BillX.Service;

import com.BillX.Exception.UserException;
import com.BillX.Model.User;
import com.BillX.Payload.dto.BranchDto;
import java.util.List;


public interface BranchService {
    BranchDto createBranch(BranchDto branchDto,User user) throws UserException;
    BranchDto updateBranch(Long id, BranchDto branchDto) throws Exception;
    void deleteBranch(Long id) throws Exception;
    List<BranchDto> getAllBranchesByStoreId(Long storeId);
    BranchDto getByBranchId(Long branchId) throws Exception;
}
