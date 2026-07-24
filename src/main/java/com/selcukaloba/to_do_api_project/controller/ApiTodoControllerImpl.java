package com.selcukaloba.to_do_api_project.controller;

import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoCreateRequest;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoResponse;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoShareRequestResponse;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoUpdateRequest;
import com.selcukaloba.to_do_api_project.service.ITodoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/rest/api/todo")
public class ApiTodoControllerImpl implements IApiTodoController {

    @Autowired
    private ITodoService todoService;

    @PostMapping(path = "/create")
    @Override
    public ApiTodoResponse createTodo(@Valid @RequestBody ApiTodoCreateRequest request, Principal principal) {
        String loginuser = principal.getName();
        return todoService.createTodo(request, loginuser);
    }

    @GetMapping(path = "/all")
    @Override
    public List<ApiTodoResponse> getAllTodo(Principal principal)
    {
        String loginuser = principal.getName();
        return todoService.getAllTodo(loginuser);
    }

    @PutMapping(path = "/update/{id}")
    @Override
    public ApiTodoResponse updateTodo(@PathVariable(name = "id") Long id, @Valid @RequestBody ApiTodoUpdateRequest request, Principal principal) {
        String loginuser = principal.getName();
        return todoService.updateTodo(id, request, loginuser);
    }

    @DeleteMapping(path = "/delete/{id}")
    @Override
    public void deleteTodo(@PathVariable(name = "id") Long id, Principal principal)
    {
        String loginuser = principal.getName();
        todoService.deleteTodo(id, loginuser);
    }

    @GetMapping(path = "/upcomings")
    @Override
    public List<ApiTodoResponse> getUpcomingReminders(Principal prinicpal, @RequestParam(name = "days", required = false, defaultValue = "7") int days) {
        String loginuser = prinicpal.getName();
        return todoService.getUpcomingReminders(loginuser, days);
    }

    @PostMapping(path = "/share/{id}")
    @Override
    public void shareTodoWithFriend(@PathVariable(name = "id") Long id, Principal principal, @RequestParam String friendUsername) {
        String loginuser = principal.getName();
        todoService.shareTodoWithFriend(id, loginuser, friendUsername);
    }

    @GetMapping(path = "/shared/all")
    @Override
    public List<ApiTodoResponse> getSharedTodos(Principal principal) {
        String loginuser = principal.getName();
        return todoService.getSharedTodos(loginuser);
    }

    @GetMapping(path = "/share/pending")
    @Override
    public List<ApiTodoShareRequestResponse> getPendingShareRequests(Principal principal) {
        String loginuser = principal.getName();
        return todoService.getPendingShareRequests(loginuser);
    }

    @PutMapping(path = "/share/accept/{requestId}")
    @Override
    public void acceptShareRequest(@PathVariable(name = "requestId") Long requestId, Principal principal) {
        String loginuser = principal.getName();
        todoService.acceptShareRequest(requestId, loginuser);
    }

    @PutMapping(path = "/share/reject/{requestId}")
    @Override
    public void rejectShareRequest(@PathVariable(name = "requestId")Long requestId, Principal principal) {
        String loginuser = principal.getName();
        todoService.rejectShareRequest(requestId, loginuser);
    }
}
