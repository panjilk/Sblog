package com.sblogjava.service;

import com.sblogjava.Dto.CommentCreateRequest;
import com.sblogjava.Dto.CommentDto;
import com.sblogjava.Dto.CommentPageResult;
import com.sblogjava.Dto.ReplyRequest;
import com.sblogjava.dao.Article;
import com.sblogjava.dao.ArticleRepository;
import com.sblogjava.dao.Comment;
import com.sblogjava.dao.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleService articleService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public CommentPageResult getComments(Integer page, Integer pageSize, String status, Long articleId) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        Comment.CommentStatus statusEnum = null;
        if (status != null && !status.isEmpty()) {
            statusEnum = Comment.CommentStatus.valueOf(status.toUpperCase());
        }

        Page<Comment> commentPage = commentRepository.searchComments(statusEnum, articleId, pageable);

        CommentPageResult result = new CommentPageResult();
        result.setTotal(commentPage.getTotalElements());
        result.setList(commentPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList()));

        return result;
    }

    @Override
    @Transactional
    public void replyComment(Long id, ReplyRequest request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("评论不存在"));

        // 这里可以创建一个回复记录，暂时简化为更新原评论
        // 实际项目中可能需要创建单独的 Reply 表
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        Long articleId = comment.getArticle().getId();
        commentRepository.deleteById(id);
        // 减少文章评论数
        articleService.updateCommentCount(articleId, -1);
    }

    @Override
    public long getTotalCount() {
        return commentRepository.count();
    }

    @Override
    @Transactional
    public void createComment(CommentCreateRequest request, String ip) {
        // 验证文章是否存在
        Article article = articleRepository.findById(request.getArticleId())
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        // 创建评论
        Comment comment = new Comment();
        comment.setAuthor(request.getAuthor());
        comment.setContent(request.getContent());
        comment.setEmail(request.getEmail());
        comment.setArticle(article);
        comment.setIp(ip);
        // 默认状态为 APPROVED（已通过，无需审核）
        comment.setStatus(Comment.CommentStatus.APPROVED);

        commentRepository.save(comment);
        // 增加文章评论数
        articleService.updateCommentCount(request.getArticleId(), 1);
    }

    @Override
    public CommentPageResult getApprovedComments(Long articleId) {
        // 只返回已审核通过的评论
        return getComments(1, 100, "approved", articleId);
    }

    private CommentDto convertToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setAuthor(comment.getAuthor());
        dto.setEmail(comment.getEmail());
        dto.setStatus(comment.getStatus().name().toLowerCase());
        dto.setIp(comment.getIp());
        dto.setCreatedAt(comment.getCreatedAt() != null ?
                comment.getCreatedAt().format(formatter) : null);

        if (comment.getArticle() != null) {
            dto.setArticleId(comment.getArticle().getId());
            dto.setArticleTitle(comment.getArticle().getTitle());
        }

        return dto;
    }
}
