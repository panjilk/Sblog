package com.sblogjava.Controller;

import com.sblogjava.Dto.PasswordChangeRequest;
import com.sblogjava.common.Result;
import com.sblogjava.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody PasswordChangeRequest request) {
        try {
            // 从安全上下文获取当前用户名
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication != null ? authentication.getName() : null;

            if (username == null) {
                return Result.error(401, "未登录");
            }

            authService.changePassword(username, request);
            return Result.success("密码修改成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
