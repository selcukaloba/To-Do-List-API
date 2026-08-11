package com.selcukaloba.to_do_api_project.repository;

import com.selcukaloba.to_do_api_project.dto.ApiCommentResponse;
import com.selcukaloba.to_do_api_project.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTodoIdOrderByCreatedAtAsc(Long todoId);
    int countByTodoId(Long todoId);
    void deleteByTodoId(Long todoId);
}
