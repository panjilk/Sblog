package com.sblogjava.service;

import com.sblogjava.Dto.AdminUserRequest;
import com.sblogjava.Dto.RoleDto;
import com.sblogjava.Dto.UserDto;
import com.sblogjava.Dto.UserPageResult;

import java.util.List;

public interface AdminUserService {
    UserPageResult getUsers(Integer page, Integer pageSize, String keyword);

    void createUser(AdminUserRequest request);

    void updateUser(Long id, AdminUserRequest request);

    void deleteUser(Long id);

    List<RoleDto> getRoles();
}
