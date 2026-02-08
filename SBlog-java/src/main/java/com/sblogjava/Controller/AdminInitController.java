package com.sblogjava.Controller;

import com.sblogjava.common.Result;
import com.sblogjava.dao.User;
import com.sblogjava.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * 临时管理员初始化控制器
 * 用于修复数据库密码问题，修复后可删除此文件
 */
@RestController
@RequestMapping("/api/admin/init")
public class AdminInitController {

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

    /**
     * 重置管理员密码
     * 用户名: admin
     * 新密码: admin123
     */
    @PostMapping("/reset-admin")
    public Result<Void> resetAdminPassword() {
        Optional<User> adminOpt = userRepository.findByUsername("admin");

        if (adminOpt.isEmpty()) {
            // 创建管理员账户
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.UserRole.ADMIN);
            admin.setEmail(null);
            userRepository.save(admin);
            return Result.success("管理员账户创建成功", null);
        }

        // 重置密码
        User admin = adminOpt.get();
        admin.setPassword(passwordEncoder.encode("admin123"));
        if (admin.getEmail() != null && admin.getEmail().isEmpty()) {
            admin.setEmail(null);
        }
        userRepository.save(admin);

        return Result.success("管理员密码重置成功", null);
    }
}
