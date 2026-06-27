package com.BillX.controller;

import com.BillX.Exception.UserException;
import com.BillX.Payload.dto.UserDto;
import com.BillX.Payload.request.RegisterRequest;
import com.BillX.Payload.response.AuthResponse;
import com.BillX.Service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({ "/auth", "/api/auth" })
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping({ "/register", "/signup" })
    public ResponseEntity<AuthResponse> signupHandler(@Valid @RequestBody RegisterRequest registerRequest)
            throws UserException {
        UserDto userDto = new UserDto();
        userDto.setFullName(registerRequest.getName());
        userDto.setEmail(registerRequest.getEmail());
        userDto.setPassword(registerRequest.getPassword());

        AuthResponse response = authService.signup(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginHandler(@RequestBody UserDto userDto) throws UserException {
        return ResponseEntity.ok(authService.login(userDto));
    }
}
