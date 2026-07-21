package com.selcukaloba.to_do_api_project.controller;

import com.selcukaloba.to_do_api_project.dto.todo.TodoCreateRequest;
import com.selcukaloba.to_do_api_project.dto.todo.TodoResponse;
import com.selcukaloba.to_do_api_project.dto.todo.TodoShareRequestResponse;
import com.selcukaloba.to_do_api_project.dto.todo.TodoUpdateRequest;

import java.security.Principal;
import java.util.List;

public interface ITodoController {
    TodoResponse createTodo(TodoCreateRequest request, Principal principal);
    List<TodoResponse> getAllTodo(Principal principal);
    TodoResponse updateTodo(Long id, TodoUpdateRequest request);
    void deleteTodo(Long id);
    List<TodoResponse>getUpcomingReminders(int days);
    void shareTodoWithFriend(Long id, Principal principal, String friendUsername);
    public List<TodoResponse> getSharedTodos(Principal principal);
    List<TodoShareRequestResponse>getPendingShareRequests(Principal principal);
    void acceptShareRequest(Long requestId);
    void rejectShareRequest(Long requestId);

}
