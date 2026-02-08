package com.sblogjava.Dto;

import lombok.Data;

@Data
public class MessageDto {
    private Long id;
    private String content;
    private String nickname;
    private String contact;
    private String status;
    private String reply;
    private String createdAt;
}
