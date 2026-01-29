package com.BillX.Service.impl;

import com.BillX.Exception.UserException;
import com.BillX.Model.User;
import com.BillX.Repository.UserRepository;
import com.BillX.Service.UserService;
import com.BillX.configuration.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Override
    public User getUserFromJwt(String token) throws UserException {
        String email = jwtProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new UserException("Invalid Token");
        }
        return user;
    }

    @Override
    public User getCurrentUsers() throws UserException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        if(user==null){
            throw new UserException("User Not Found");
        }
        return user;
    }

    @Override
    public User getUserByEmail(String email) throws UserException {
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new UserException("User Not Found");
        }
        return user;
    }

    @Override
    public User getUserById(Long id) throws UserException, Exception {
        return userRepository.findById(id)
                .orElseThrow(() -> new Exception("User not found "));
    }


    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
