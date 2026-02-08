package com.sblogjava.service;

import com.sblogjava.Dto.CommentCreateRequest;
import com.sblogjava.Dto.CommentDto;
import com.sblogjava.Dto.CommentPageResult;
import com.sblogjava.Dto.ReplyRequest;

public interface CommentService {
    CommentPageResult getComments(Integer page, Integer pageSize, String status, Long articleId);

    void replyComment(Long id, ReplyRequest request);

    void deleteComment(Long id);

    long getTotalCount();

    // 前台评论接口
    void createComment(CommentCreateRequest request, String ip);

    CommentPageResult getApprovedComments(Long articleId);
}
