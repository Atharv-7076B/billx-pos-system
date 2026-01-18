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

        User newUser = new User();
        newUser.setEmail(userDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        newUser.setRole(UserRole.ROLE_USER);
        newUser.setFullName(userDto.getFullName());
        newUser.setPhoneNumber(userDto.getPhoneNumber());
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        User savedUser = repo.save(newUser);

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

        AuthResponse response = new AuthResponse();
        response.setJwt(jwt);
        response.setMessage("Registered Successfully");
        response.setUserDto(UserMapper.toDTO(savedUser));

        return response;
    }

    @Override
    public AuthResponse login(UserDto userDto) throws UserException {

        Authentication authentication =
                authenticate(userDto.getEmail(), userDto.getPassword());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = provider.generateToken(authentication);

        User user = repo.findByEmail(userDto.getEmail());

        AuthResponse response = new AuthResponse();
        response.setJwt(jwt);
        response.setMessage("Login Successfully");
        response.setUserDto(UserMapper.toDTO(user));

        return response;
    }

    private Authentication authenticate(String email, String password) throws UserException {

        UserDetails userDetails =
                customUserImplementation.loadUserByUsername(email);

        if (userDetails == null) {
            throw new UserException("Email does not exist: " + email);
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
//import jdk.jshell.spi.ExecutionControl;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.Collection;
//
//@Service
//@RequiredArgsConstructor
//public class AuthServiceimpl implements AuthService {
//
//    private final UserRepository repo;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtProvider provider;
//    private final CustomUserImplementation customUserImplementation;
//
//    @Override
//    public AuthResponse signup(UserDto userDto) throws UserException {
//        User user = repo.findByEmail(userDto.getEmail());
//        if (user != null) {
//            throw new UserException("Email already Exists");
//        }
//        if (userDto.getRole().equals(UserRole.ROLE_ADMIN)) {
//            throw new UserException("Role admin is not allowed");
//        }
//        User newUser = new User();
//        newUser.setEmail(userDto.getEmail());
//        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
//        newUser.setRole(UserRole.ROLE_USER);
//        newUser.setFullName(userDto.getFullName());
//        newUser.setPhoneNumber(userDto.getPhoneNumber()); // Assuming UserDto has phoneNumber
//        newUser.setLastLogin(LocalDateTime.now());
//        newUser.setCreatedAt(LocalDateTime.now());
//        newUser.setUpdatedAt(LocalDateTime.now());
//        User savedUser = repo.save(newUser);
//
//        Authentication authentication = new UsernamePasswordAuthenticationToken(userDto.getEmail(),
//                userDto.getPassword());
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        String jwt = provider.generateToken(authentication);
//
//        AuthResponse authResponse = new AuthResponse();
//        authResponse.setJwt(jwt);
//        authResponse.setMessage("Registered Successfully");
//        authResponse.setUserDto(UserMapper.toDTO(savedUser));
//        return authResponse;
//    }
//
//    @Override
//    public AuthResponse login(UserDto userDto) throws UserException {
//        String email = userDto.getEmail();
//        String password = userDto.getPassword();
//
//        Authentication authentication = authenticate(email, password);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//        String role = authorities.iterator().next().getAuthority().toString();
//        String jwt = provider.generateToken(authentication);
//        User user = repo.findByEmail(email);
//
//        AuthResponse authResponse = new AuthResponse();
//        authResponse.setJwt(jwt);
//        authResponse.setMessage("Login Successfully");
//        authResponse.setUserDto(UserMapper.toDTO(user));
//        return authResponse;
//    }
//
//    private Authentication authenticate(String email, String password) throws UserException {
//        UserDetails userDetails = customUserImplementation.loadUserByUsername(email);
//        if (userDetails == null) {
//            throw new UserException("Email doesn't exists" + email);
//        }
//        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
//            throw new UserException("Password doesn't match");
//        }
//        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//    }
//}
