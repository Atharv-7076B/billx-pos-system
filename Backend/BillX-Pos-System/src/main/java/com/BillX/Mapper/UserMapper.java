package com.BillX.Mapper;

import com.BillX.Model.User;
import com.BillX.Payload.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public static UserDto toDTO(User user) {
        if (user == null) return null;

        UserDto userDto = new UserDto();
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

    public static User toEntity(UserDto userDto) {
        if (userDto == null) return null;

        User user = new User();
        user.setId(userDto.getId());
        user.setFullName(userDto.getFullName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setRole(userDto.getRole());
        user.setCreatedAt(userDto.getCreatedAt());
        user.setUpdatedAt(userDto.getUpdatedAt());
        user.setLastLogin(userDto.getLastLogin());
        user.setPhoneNumber(userDto.getPhoneNumber());

        return user;
    }
}