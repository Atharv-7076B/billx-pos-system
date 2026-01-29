//package com.BillX.Service.impl;
//
//import com.BillX.Exception.UserException;
//import com.BillX.Mapper.UserMapper;
//import com.BillX.Model.User;
//import com.BillX.Payload.dto.UserDto;
//import com.BillX.Payload.response.AuthResponse;
//import com.BillX.Repository.UserRepository;
//import com.BillX.Service.AuthService;
//import com.BillX.configuration.JwtProvider;
//import com.BillX.domain.UserRole;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//
//@Service
//@RequiredArgsConstructor
//public class AuthServiceImpl implements AuthService {
//
//    private final UserRepository repo;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtProvider provider;
//    private final CustomUserImplementation customUserImplementation;
//
//    @Override
//    public AuthResponse signup(UserDto userDto) throws UserException {
//
//        if (repo.findByEmail(userDto.getEmail()) != null) {
//            throw new UserException("Email already exists");
//        }
//
//        User newUser = new User();
//        newUser.setEmail(userDto.getEmail());
//        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
//        newUser.setRole(UserRole.ROLE_USER);
//        newUser.setFullName(userDto.getFullName());
//        newUser.setPhoneNumber(userDto.getPhoneNumber());
//        newUser.setCreatedAt(LocalDateTime.now());
//        newUser.setUpdatedAt(LocalDateTime.now());
//
//        User savedUser = repo.save(newUser);
//
//        UserDetails userDetails =
//                customUserImplementation.loadUserByUsername(savedUser.getEmail());
//
//        Authentication authentication =
//                new UsernamePasswordAuthenticationToken(
//                        userDetails,
//                        null,
//                        userDetails.getAuthorities()
//                );
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        String jwt = provider.generateToken(authentication);
//
//        AuthResponse response = new AuthResponse();
//        response.setJwt(jwt);
//        response.setMessage("Registered Successfully");
//        response.setUserDto(UserMapper.toDTO(savedUser));
//
//        return response;
//    }
//
//    @Override
//    public AuthResponse login(UserDto userDto) throws UserException {
//
//        Authentication authentication =
//                authenticate(userDto.getEmail(), userDto.getPassword());
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        String jwt = provider.generateToken(authentication);
//
//        User user = repo.findByEmail(userDto.getEmail());
//
//        AuthResponse response = new AuthResponse();
//        response.setJwt(jwt);
//        response.setMessage("Login Successfully");
//        response.setUserDto(UserMapper.toDTO(user));
//
//        return response;
//    }
//
//    private Authentication authenticate(String email, String password) throws UserException {
//
//        UserDetails userDetails =
//                customUserImplementation.loadUserByUsername(email);
//
//        if (userDetails == null) {
//            throw new UserException("Email does not exist: " + email);
//        }
//
//        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
//            throw new UserException("Invalid password");
//        }
//
//        return new UsernamePasswordAuthenticationToken(
//                userDetails,
//                null,
//                userDetails.getAuthorities()
//        );
//    }
//}
//
//


package com.BillX.Service.impl;

import com.BillX.Exception.UserException;
import com.BillX.Mapper.UserMapper;
import com.BillX.Model.User;
import com.BillX.Payload.dto.UserDto;
import com.BillX.Payload.response.AuthResponse;
import com.BillX.Repository.UserRepository;
import com.BillX.Service.AuthService;
import com.BillX.configuration.JwtProvider;
import com.BillX.domain.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider provider;
    private final CustomUserImplementation customUserImplementation;

    @Override
    public AuthResponse signup(UserDto userDto) throws UserException {

        if (repo.findByEmail(userDto.getEmail()) != null) {
            throw new UserException("Email already exists");
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setFullName(userDto.getFullName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setRole(UserRole.ROLE_USER);

        User savedUser = repo.save(user);

        UserDetails userDetails =
                customUserImplementation.loadUserByUsername(savedUser.getEmail());

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = provider.generateToken(authentication);

        return new AuthResponse(jwt, "Registered Successfully", UserMapper.toDTO(savedUser));
    }

    @Override
    public AuthResponse login(UserDto userDto) throws UserException {

        Authentication authentication =
                authenticate(userDto.getEmail(), userDto.getPassword());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = repo.findByEmail(userDto.getEmail());

        if (user == null) {
            throw new UserException("User not found");
        }

        user.setLastLogin(LocalDateTime.now());
        repo.save(user);

        String jwt = provider.generateToken(authentication);

        return new AuthResponse(jwt, "Login Successfully", UserMapper.toDTO(user));
    }

    private Authentication authenticate(String email, String password) throws UserException {

        UserDetails userDetails =
                customUserImplementation.loadUserByUsername(email);

        if (userDetails == null) {
            throw new UserException("Email not found");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new UserException("Invalid password");
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}
