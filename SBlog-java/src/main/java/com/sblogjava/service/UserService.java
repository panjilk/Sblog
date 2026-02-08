package com.sblogjava.service;

import com.sblogjava.Dto.LoginRequest;
import com.sblogjava.Dto.LoginResponse;
import com.sblogjava.Dto.RegisterRequest;
import com.sblogjava.Dto.UserDto;
import com.sblogjava.dao.User;

import java.util.Optional;

public interface UserService {
    Optional<UserDto> findByUsername(String username);

    Optional<UserDto> findByid(long id);

    void register(RegisterRequest registerRequest);

    LoginResponse login(LoginRequest loginRequest);

    boolean existsByUsername(String username);

    void logout(String token);
}
