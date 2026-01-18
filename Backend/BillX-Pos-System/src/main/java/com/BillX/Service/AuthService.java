package com.BillX.Service;

import com.BillX.Exception.UserException;
import com.BillX.Payload.dto.UserDto;
import com.BillX.Payload.response.AuthResponse;

public interface AuthService {
    AuthResponse signup(UserDto userDto) throws UserException;
    AuthResponse login(UserDto userDto) throws UserException;
}
