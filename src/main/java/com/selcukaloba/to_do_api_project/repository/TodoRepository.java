package com.selcukaloba.to_do_api_project.repository;

import com.selcukaloba.to_do_api_project.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByIsCompletedFalseAndReminderDateBetween(LocalDateTime start, LocalDateTime end);
    List<Todo> findByIsCompletedFalse();
    List<Todo> findByUserUsernameOrSharedUsersUsername(String ownerUsername, String sharedUsername);
    @Query("SELECT DISTINCT t FROM Todo t LEFT JOIN t.sharedUsers su WHERE t.user.username = :username OR su.username = :username")
    List<Todo> findAllTodosByOwnerOrSharedUser(@Param("username")String username);
    List<Todo> findByUserUsername(String username);
}
