package com.sblogjava.Controller;

import com.sblogjava.Dto.FriendLinkDto;
import com.sblogjava.common.Result;
import com.sblogjava.service.FriendLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台友情链接接口（不需要登录）
 */
@RestController
@RequestMapping("/api/friend-links")
public class PublicFriendLinkController {

    @Autowired
    private FriendLinkService friendLinkService;

    @GetMapping
    public Result<List<FriendLinkDto>> list() {
        return Result.success(friendLinkService.getActiveLinks());
    }
}
