package com.BillX.Repository;

import com.BillX.Model.Branch;
import com.BillX.Model.Store;
import com.BillX.Model.User;
import com.BillX.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String username);

    List<User> findByStore(Store store);
    List<User> findByBranch(Branch branch);

//    List<User> findByStoreAndRoleIn(Store store, List<UserRole> roleBranchManager);
}
