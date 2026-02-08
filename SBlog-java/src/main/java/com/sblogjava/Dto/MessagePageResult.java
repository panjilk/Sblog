package com.sblogjava.Dto;

import lombok.Data;

import java.util.List;

@Data
public class MessagePageResult {
    private Long total;
    private List<MessageDto> list;
}
