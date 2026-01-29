package com.BillX.controller;

import com.BillX.Exception.UserException;
import com.BillX.Mapper.UserMapper;
import com.BillX.Model.User;
import com.BillX.Payload.dto.UserDto;
import com.BillX.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    public final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getUserProfile(@RequestHeader("Authorization") String jwtToken) throws UserException {
        User user = userService.getUserFromJwt(jwtToken);
        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@RequestHeader("Authorization") String jwtToken, @PathVariable Long id) throws UserException, Exception {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

}
