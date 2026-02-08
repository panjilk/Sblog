package com.sblogjava.Controller;

import com.sblogjava.Dto.CommentDto;
import com.sblogjava.Dto.CommentPageResult;
import com.sblogjava.Dto.ReplyRequest;
import com.sblogjava.common.Result;
import com.sblogjava.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    public Result<CommentPageResult> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long articleId) {
        return Result.success(commentService.getComments(page, pageSize, status, articleId));
    }

    @PostMapping("/{id}/reply")
    public Result<Void> reply(@PathVariable Long id, @RequestBody ReplyRequest request) {
        try {
            commentService.replyComment(id, request);
            return Result.success("回复成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            commentService.deleteComment(id);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
