package com.selcukaloba.to_do_api_project.controller;

import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoCreateRequest;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoResponse;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoShareRequestResponse;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoUpdateRequest;

import java.security.Principal;
import java.util.List;

public interface IApiTodoController {
    ApiTodoResponse createTodo(ApiTodoCreateRequest request, Principal principal);
    List<ApiTodoResponse> getAllTodo(Principal principal);
    ApiTodoResponse updateTodo(Long id, ApiTodoUpdateRequest request);
    void deleteTodo(Long id);
    List<ApiTodoResponse>getUpcomingReminders(Principal principal, int days);
    void shareTodoWithFriend(Long id, Principal principal, String friendUsername);
    public List<ApiTodoResponse> getSharedTodos(Principal principal);
    List<ApiTodoShareRequestResponse>getPendingShareRequests(Principal principal);
    void acceptShareRequest(Long requestId);
    void rejectShareRequest(Long requestId);

}
