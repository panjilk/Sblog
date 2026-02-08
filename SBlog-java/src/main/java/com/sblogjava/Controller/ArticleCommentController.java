package com.sblogjava.Controller;

import com.sblogjava.Dto.CommentCreateRequest;
import com.sblogjava.Dto.CommentPageResult;
import com.sblogjava.common.Result;
import com.sblogjava.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
public class ArticleCommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 前台 - 提交评论
     * 不需要登录，允许访客评论
     */
    @PostMapping("/{id}/comments")
    public Result<Void> createComment(
            @PathVariable Long id,
            @RequestBody CommentCreateRequest request,
            HttpServletRequest httpRequest) {
        try {
            // 设置文章ID
            request.setArticleId(id);

            // 获取访客IP
            String ip = getClientIp(httpRequest);
            commentService.createComment(request, ip);

            return Result.success("评论发表成功！", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 前台 - 获取文章的已审核评论
     * 只返回 status = APPROVED 的评论
     */
    @GetMapping("/{id}/comments")
    public Result<CommentPageResult> getApprovedComments(@PathVariable Long id) {
        try {
            CommentPageResult result = commentService.getApprovedComments(id);
            return Result.success(result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多个IP的情况（X-Forwarded-For可能包含多个IP）
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
