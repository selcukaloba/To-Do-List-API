package com.selcukaloba.to_do_api_project.repository;

import com.selcukaloba.to_do_api_project.entity.Todo;
import com.selcukaloba.to_do_api_project.entity.TodoShareRequest;
import com.selcukaloba.to_do_api_project.enums.TodoShareStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoShareRequestRepository extends JpaRepository<TodoShareRequest, Long> {
    List<TodoShareRequest> findByReceiverUsernameAndStatus(String username, TodoShareStatus status);
}
