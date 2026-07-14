package com.selcukaloba.to_do_api_project.service;

import com.selcukaloba.to_do_api_project.dto.TodoCreateRequest;
import com.selcukaloba.to_do_api_project.dto.TodoResponse;
import com.selcukaloba.to_do_api_project.dto.TodoUpdateRequest;

import java.util.List;

public interface ITodoService {
    public TodoResponse createTodo(TodoCreateRequest request);
    public List<TodoResponse> getAllTodo();
    public TodoResponse updateTodo(Long id, TodoUpdateRequest request);
    public void deleteTodo(Long id);
    public List<TodoResponse>getUpcomingReminders(int days);
    void shareTodoWithFriend(Long id, String ownerUsername, String friendUsername);
    List<TodoResponse>getSharedTodos(String username);
}
