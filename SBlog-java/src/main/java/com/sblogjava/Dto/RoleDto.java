package com.sblogjava.Dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleDto {
    private String value;
    private String label;
    private List<String> permissions;
}
