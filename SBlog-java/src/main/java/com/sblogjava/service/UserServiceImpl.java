package com.sblogjava.service;

import com.sblogjava.Dto.LoginRequest;
import com.sblogjava.Dto.LoginResponse;
import com.sblogjava.Dto.RegisterRequest;
import com.sblogjava.Dto.UserDto;
import com.sblogjava.convert.UserConvert;
import com.sblogjava.dao.User;
import com.sblogjava.dao.UserRepository;
import com.sblogjava.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisService redisService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Optional<UserDto> findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        UserDto userDto = new UserDto();
        userDto = UserConvert.convert(user);
        return Optional.of(userDto);
    }

    @Override
    public Optional<UserDto> findByid(long id) {
        Optional<User> user = userRepository.findById(id);
        UserDto userDto = new UserDto();
        userDto = UserConvert.convert(user);
        return Optional.of(userDto);
    }

    @Override
    public void register(RegisterRequest registerRequest) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建新用户，默认状态为禁用
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(User.UserRole.USER);
        user.setStatus(User.UserStatus.DISABLED);  // 设置为禁用状态

        userRepository.save(user);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // 根据用户名查找用户
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());

        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户名或密码错误");
        }

        User user = userOpt.get();

        // 检查用户状态
        if (user.getStatus() == User.UserStatus.DISABLED) {
            throw new RuntimeException("账户已被禁用，请联系管理员");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 生成 Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());

        // 存入 Redis（7天过期）
        String redisKey = "login:token:" + user.getId();
        redisService.set(redisKey, token, 7, TimeUnit.DAYS);

        // 转换为 UserDto
        UserDto userDto = UserConvert.convert(Optional.of(user));

        return new LoginResponse(token, userDto);
    }

    @Override
    public void logout(String token) {
        // 从 token 中获取用户 ID
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId != null) {
            // 删除 Redis 中的 token
            String redisKey = "login:token:" + userId;
            redisService.delete(redisKey);
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
