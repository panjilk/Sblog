package com.sblogjava.service;

import com.sblogjava.Dto.AdminUserRequest;
import com.sblogjava.Dto.RoleDto;
import com.sblogjava.Dto.UserDto;
import com.sblogjava.Dto.UserPageResult;
import com.sblogjava.dao.User;
import com.sblogjava.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public UserPageResult getUsers(Integer page, Integer pageSize, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isEmpty()) {
                predicates.add(cb.or(
                        cb.like(root.get("username"), "%" + keyword + "%"),
                        cb.like(root.get("email"), "%" + keyword + "%")
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<User> userPage = userRepository.findAll(spec, pageable);

        UserPageResult result = new UserPageResult();
        result.setTotal(userPage.getTotalElements());
        result.setList(userPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList()));

        return result;
    }

    @Override
    public void createUser(AdminUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        if (request.getRole() != null) {
            user.setRole(User.UserRole.valueOf(request.getRole().toUpperCase()));
        } else {
            user.setRole(User.UserRole.USER);
        }
        userRepository.save(user);
    }

    @Override
    public void updateUser(Long id, AdminUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        if (request.getRole() != null) {
            user.setRole(User.UserRole.valueOf(request.getRole().toUpperCase()));
        }

        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("用户不存在");
        }
        userRepository.deleteById(id);
    }

    @Override
    public List<RoleDto> getRoles() {
        return Arrays.asList(
                createRoleDto("admin", "管理员", Arrays.asList("*")),
                createRoleDto("editor", "编辑", Arrays.asList("article:*", "comment:*")),
                createRoleDto("user", "普通用户", Arrays.asList())
        );
    }

    private RoleDto createRoleDto(String value, String label, List<String> permissions) {
        RoleDto dto = new RoleDto();
        dto.setValue(value);
        dto.setLabel(label);
        dto.setPermissions(permissions);
        return dto;
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(String.valueOf(user.getRole()).toLowerCase());
        dto.setStatus(user.getStatus() != null ? user.getStatus().name().toLowerCase() : "active");
        dto.setAvatar(user.getAvatar());
        return dto;
    }
}
