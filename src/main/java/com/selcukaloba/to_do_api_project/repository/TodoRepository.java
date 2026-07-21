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
    @Query("SELECT t FROM Todo t WHERE t.user.username = :username " +
            "OR EXISTS (SELECT ts FROM TodoShare ts WHERE ts.todo = t AND ts.sharedUser.username = :username)")
    List<Todo> findAllTodosByOwnerOrSharedUser(@Param("username") String username);
    @Query("SELECT t FROM Todo t WHERE t.isCompleted = false " +
            "AND t.reminderDate BETWEEN :startDate AND :endDate " +
            "AND (t.user.username = :username OR EXISTS (SELECT ts FROM TodoShare ts WHERE ts.todo = t AND ts.sharedUser.username = :username))")
    List<Todo> findUpcomingRemindersByUser(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("username") String username
    );
    List<Todo> findByUserUsername(String username);
}
