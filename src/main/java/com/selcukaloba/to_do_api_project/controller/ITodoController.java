package com.selcukaloba.to_do_api_project.controller;

import com.selcukaloba.to_do_api_project.dto.TodoCreateRequest;
import com.selcukaloba.to_do_api_project.dto.TodoResponse;
import com.selcukaloba.to_do_api_project.dto.TodoUpdateRequest;

import java.util.List;

public interface ITodoController {
    TodoResponse createTodo(TodoCreateRequest request);
    List<TodoResponse> getAllTodo();
    TodoResponse updateTodo(Long id, TodoUpdateRequest request);
    void deleteTodo(Long id);
    List<TodoResponse>getUpcomingReminders(int days);
}
