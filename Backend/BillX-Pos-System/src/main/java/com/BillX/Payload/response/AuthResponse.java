package com.BillX.Payload.response;

import com.BillX.Payload.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthResponse {

    private String jwt;
    private String message;
    private UserDto userDto;

}
