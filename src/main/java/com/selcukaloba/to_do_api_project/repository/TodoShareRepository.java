package com.selcukaloba.to_do_api_project.repository;

import com.selcukaloba.to_do_api_project.entity.Todo;
import com.selcukaloba.to_do_api_project.entity.TodoShare;
import com.selcukaloba.to_do_api_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoShareRepository extends JpaRepository<TodoShare, Long> {
    boolean existsByTodoAndSharedUser(Todo todo, User sharedUser);
}
