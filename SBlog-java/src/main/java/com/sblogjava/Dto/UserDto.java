package com.sblogjava.Dto;

import lombok.Data;

@Data
public class UserDto {
    private String username;
    private long id;
    private String avatar;
    private String email;
    private String role;
    private String status;
}
