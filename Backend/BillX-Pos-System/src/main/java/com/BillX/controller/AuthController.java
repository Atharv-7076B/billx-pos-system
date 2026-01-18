package com.BillX.controller;

import com.BillX.Exception.UserException;
import com.BillX.Payload.dto.UserDto;
import com.BillX.Payload.response.AuthResponse;
import com.BillX.Service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signupHandler(@RequestBody UserDto userDto) throws UserException {
        return ResponseEntity.ok(authService.signup(userDto));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginHandler(@RequestBody UserDto userDto) throws UserException{
        return ResponseEntity.ok(authService.login(userDto));
    }
}
