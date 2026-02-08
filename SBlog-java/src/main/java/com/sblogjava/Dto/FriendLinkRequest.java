package com.sblogjava.Dto;

import lombok.Data;

@Data
public class FriendLinkRequest {
    private String name;
    private String url;
    private String logo;
    private String description;
    private Integer sortOrder;
    private Boolean isActive;
}
