package com.BillX.Mapper;

import com.BillX.Model.User;
import com.BillX.Payload.dto.UserDto;

public class UserMapper {

    public static User toDTO(User user) {
        User userDto = new User();
        userDto.setId(user.getId());
        userDto.setFullName(user.getFullName());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole());
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setUpdatedAt(user.getUpdatedAt());
        userDto.setLastLogin(user.getLastLogin());
        userDto.setPhoneNumber(user.getPhoneNumber());
        return userDto;
    }
}
