package com.selcukaloba.to_do_api_project.controller;

import com.selcukaloba.to_do_api_project.dto.TodoCreateRequest;
import com.selcukaloba.to_do_api_project.dto.TodoResponse;
import com.selcukaloba.to_do_api_project.dto.TodoUpdateRequest;
import com.selcukaloba.to_do_api_project.service.ITodoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/api/todo")
public class TodoControllerImpl implements ITodoController {

    @Autowired
    private ITodoService todoService;

    @PostMapping(path = "/create")
    @Override
    public TodoResponse createTodo(@Valid @RequestBody TodoCreateRequest request) {
        return todoService.createTodo(request);
    }

    @GetMapping(path = "/all")
    @Override
    public List<TodoResponse> getAllTodo() {
        return todoService.getAllTodo();
    }

    @PutMapping(path = "/update/{id}")
    @Override
    public TodoResponse updateTodo(@PathVariable(name = "id") Long id,@Valid @RequestBody TodoUpdateRequest request) {
        return todoService.updateTodo(id, request);
    }

    @DeleteMapping(path = "/delete/{id}")
    @Override
    public void deleteTodo(@PathVariable(name = "id") Long id) {
        todoService.deleteTodo(id);
    }

    @GetMapping(path = "/upcomings")
    @Override
    public List<TodoResponse> getUpcomingReminders(@RequestParam(name = "days", required = false, defaultValue = "7") int days) {
        return todoService.getUpcomingReminders(days);
    }
}
