package com.sblogjava.Controller;

import com.sblogjava.Dto.LoginRequest;
import com.sblogjava.Dto.LoginResponse;
import com.sblogjava.Dto.RegisterRequest;
import com.sblogjava.Dto.UserDto;
import com.sblogjava.common.Result;
import com.sblogjava.convert.UserConvert;
import com.sblogjava.dao.User;
import com.sblogjava.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/admin/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/find/{id}")
    public Result<UserDto> getUser(@PathVariable long id) {
        Optional<UserDto> userDto = userService.findByid(id);
        return userDto.map(Result::success)
                .orElse(Result.error("用户不存在"));
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterRequest registerRequest) {
        try {
            userService.register(registerRequest);
            return Result.success("注册成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = userService.login(loginRequest);
            return Result.success("登录成功", loginResponse);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return true-用户名可用, false-用户名已存在
     */
    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return Result.success("success", !exists);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String authorization) {
        try {
            // 提取 token（去掉 "Bearer " 前缀）
            String token = authorization.replace("Bearer ", "");
            userService.logout(token);
            return Result.success("退出成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
