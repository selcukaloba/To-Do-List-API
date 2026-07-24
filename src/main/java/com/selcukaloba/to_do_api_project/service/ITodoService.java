package com.selcukaloba.to_do_api_project.service;

import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoCreateRequest;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoResponse;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoShareRequestResponse;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoUpdateRequest;

import java.util.List;

public interface ITodoService {
    public ApiTodoResponse createTodo(ApiTodoCreateRequest request, String username);
    public List<ApiTodoResponse> getAllTodo(String username);
    public ApiTodoResponse updateTodo(Long id, ApiTodoUpdateRequest request, String username);
    public void deleteTodo(Long id, String username);
    public List<ApiTodoResponse>getUpcomingReminders(String username, int days);
    void shareTodoWithFriend(Long id, String ownerUsername, String friendUsername);
    List<ApiTodoResponse>getSharedTodos(String username);
    List<ApiTodoShareRequestResponse>getPendingShareRequests(String username);
    void acceptShareRequest(Long requestId, String username);
    void rejectShareRequest(Long requestId, String username);
}
