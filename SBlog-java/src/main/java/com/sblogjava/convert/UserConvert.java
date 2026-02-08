package com.sblogjava.convert;

import com.sblogjava.Dto.UserDto;
import com.sblogjava.dao.User;

import java.util.Optional;

public class UserConvert {
    public static UserDto convert(Optional<User> user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.get().getId());
        userDto.setUsername(user.get().getUsername());
        userDto.setAvatar(user.get().getAvatar());
        userDto.setRole(String.valueOf(user.get().getRole()));
        userDto.setEmail(user.get().getEmail());
        return userDto;
    }
}
