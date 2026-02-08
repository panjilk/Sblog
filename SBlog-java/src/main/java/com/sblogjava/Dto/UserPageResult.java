package com.sblogjava.Dto;

import lombok.Data;

import java.util.List;

@Data
public class UserPageResult {
    private Long total;
    private List<UserDto> list;
}
