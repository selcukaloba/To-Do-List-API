package com.selcukaloba.to_do_api_project.repository;

import com.selcukaloba.to_do_api_project.entity.Todo;
import com.selcukaloba.to_do_api_project.entity.TodoShare;
import com.selcukaloba.to_do_api_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TodoShareRepository extends JpaRepository<TodoShare, Long> {
    boolean existsByTodoAndSharedUser(Todo todo, User sharedUser);
    void deleteAllByTodoId(Long id);
    boolean existsByTodoIdAndSharedUserId(Long todoId, Long sharedUserId);
    void deleteByTodoIdAndSharedUserId(Long todoId, Long sharedUserId);
    @Modifying
    @Query("DELETE FROM TodoShare ts WHERE " +
            "(ts.sharedBy = :user1 AND ts.sharedUser = :user2) OR " +
            "(ts.sharedBy = :user2 AND ts.sharedUser = :user1)")
    void deleteAllByUsers(@Param("user1") User user1, @Param("user2") User user2);
}
