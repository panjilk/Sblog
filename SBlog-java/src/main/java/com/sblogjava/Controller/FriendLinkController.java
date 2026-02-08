package com.sblogjava.Controller;

import com.sblogjava.Dto.FriendLinkDto;
import com.sblogjava.Dto.FriendLinkRequest;
import com.sblogjava.common.Result;
import com.sblogjava.service.FriendLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/friend-links")
public class FriendLinkController {

    @Autowired
    private FriendLinkService friendLinkService;

    @GetMapping
    public Result<List<FriendLinkDto>> list() {
        return Result.success(friendLinkService.getAllLinks());
    }

    @GetMapping("/{id}")
    public Result<FriendLinkDto> getById(@PathVariable Long id) {
        try {
            return Result.success(friendLinkService.getById(id));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping
    public Result<Void> create(@RequestBody FriendLinkRequest request) {
        try {
            friendLinkService.create(request);
            return Result.success("添加成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody FriendLinkRequest request) {
        try {
            friendLinkService.update(id, request);
            return Result.success("更新成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            friendLinkService.delete(id);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
