package com.sblogjava.Controller;

import com.sblogjava.Dto.AdminUserRequest;
import com.sblogjava.Dto.RoleDto;
import com.sblogjava.Dto.UserDto;
import com.sblogjava.Dto.UserPageResult;
import com.sblogjava.common.Result;
import com.sblogjava.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @GetMapping
    public Result<UserPageResult> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(adminUserService.getUsers(page, pageSize, keyword));
    }

    @PostMapping
    public Result<Void> create(@RequestBody AdminUserRequest request) {
        try {
            adminUserService.createUser(request);
            return Result.success("创建成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody AdminUserRequest request) {
        try {
            adminUserService.updateUser(id, request);
            return Result.success("更新成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            adminUserService.deleteUser(id);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/roles")
    public Result<List<RoleDto>> getRoles() {
        return Result.success(adminUserService.getRoles());
    }
}
