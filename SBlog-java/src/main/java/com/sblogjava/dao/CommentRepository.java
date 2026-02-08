package com.sblogjava.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByStatus(Comment.CommentStatus status, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE " +
            "(:status IS NULL OR c.status = :status) " +
            "AND (:articleId IS NULL OR c.article.id = :articleId)")
    Page<Comment> searchComments(@Param("status") Comment.CommentStatus status,
                                   @Param("articleId") Long articleId,
                                   Pageable pageable);

    long countByStatus(Comment.CommentStatus status);
}
