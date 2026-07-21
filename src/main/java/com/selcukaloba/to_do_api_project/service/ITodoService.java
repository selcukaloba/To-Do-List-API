package com.selcukaloba.to_do_api_project.service;

import com.selcukaloba.to_do_api_project.dto.todo.TodoCreateRequest;
import com.selcukaloba.to_do_api_project.dto.todo.TodoResponse;
import com.selcukaloba.to_do_api_project.dto.todo.TodoShareRequestResponse;
import com.selcukaloba.to_do_api_project.dto.todo.TodoUpdateRequest;

import java.util.List;

public interface ITodoService {
    public TodoResponse createTodo(TodoCreateRequest request, String username);
    public List<TodoResponse> getAllTodo(String username);
    public TodoResponse updateTodo(Long id, TodoUpdateRequest request);
    public void deleteTodo(Long id);
    public List<TodoResponse>getUpcomingReminders(int days);
    void shareTodoWithFriend(Long id, String ownerUsername, String friendUsername);
    List<TodoResponse>getSharedTodos(String username);
    List<TodoShareRequestResponse>getPendingShareRequests(String username);
    void acceptShareRequest(Long requestId);
    void rejectShareRequest(Long requestId);
}
