package com.sblogjava.service;

import com.sblogjava.Dto.PasswordChangeRequest;

public interface AuthService {
    void changePassword(String username, PasswordChangeRequest request);
}
