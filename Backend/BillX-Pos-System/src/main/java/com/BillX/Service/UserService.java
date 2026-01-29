package com.BillX.Service;

import com.BillX.Exception.UserException;
import com.BillX.Model.User;

import java.util.List;

public interface UserService {
    User getUserFromJwt(String token) throws UserException;
    User getCurrentUsers() throws UserException;
    User getUserByEmail(String email) throws UserException;
    User getUserById(Long id) throws UserException, Exception;
    List<User> getAllUsers();
}
