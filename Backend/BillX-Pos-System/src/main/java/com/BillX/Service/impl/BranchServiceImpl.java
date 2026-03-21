package com.BillX.Service.impl;

import com.BillX.Exception.UserException;
import com.BillX.Mapper.BranchMapper;
import com.BillX.Model.Branch;
import com.BillX.Model.Store;
import com.BillX.Model.User;
import com.BillX.Payload.dto.BranchDto;
import com.BillX.Repository.BranchRepository;
import com.BillX.Repository.StoreRepository;
import com.BillX.Repository.UserRepository;
import com.BillX.Service.BranchService;
import com.BillX.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {
    private final BranchRepository branchRepository;
    private final UserService userService;
    private final StoreRepository storeRepository;
    private final BranchMapper branchMapper;

    @Override
    public BranchDto createBranch(BranchDto branchDto,User user) throws UserException {
        User currUser = userService.getCurrentUsers();
        Store store = storeRepository.findByStoreAdminId(currUser.getId());
        Branch branch = branchMapper.toEntity(branchDto,store);
        Branch newBranch = branchRepository.save(branch);
        return branchMapper.toDto(newBranch);
    }

    @Override
    public BranchDto updateBranch(Long id, BranchDto branchDto) throws Exception {
        Branch existing = branchRepository.findById(id).orElseThrow(
                ()->new Exception("Branch Not exists")
        );
        existing.setName(branchDto.getName());
        existing.setEmail(branchDto.getEmail());
        existing.setWorkingDays(branchDto.getWorkingDays());
        existing.setPhone(branchDto.getPhone());
        existing.setAddress(branchDto.getAddress());
        existing.setOpenTime(branchDto.getOpenTime());
        existing.setCloseTime(branchDto.getCloseTime());
        existing.setUpdatedAt(LocalDateTime.now());
        Branch updatedBranch = branchRepository.save(existing);
        return branchMapper.toDto(updatedBranch);
    }

    @Override
    public void deleteBranch(Long id) throws Exception {
        Branch existing = branchRepository.findById(id).orElseThrow(
                ()->new Exception("Branch Not exists")
        );
        branchRepository.delete(existing);
    }

    @Override
    public List<BranchDto> getAllBranchesByStoreId(Long storeId) {
        List<Branch> branches = branchRepository.findByStoreId(storeId);
        return branches.stream().map(BranchMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BranchDto getByBranchId(Long branchId) throws Exception {
        Branch existing = branchRepository.findById(branchId).orElseThrow(
                ()->new Exception("Branch Not exists")
        );
        return BranchMapper.toDto(existing);
    }
}
