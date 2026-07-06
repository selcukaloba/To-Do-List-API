package com.selcukaloba.to_do_api_project.service;

import com.selcukaloba.to_do_api_project.dto.TodoCreateRequest;
import com.selcukaloba.to_do_api_project.dto.TodoResponse;
import com.selcukaloba.to_do_api_project.dto.TodoUpdateRequest;
import com.selcukaloba.to_do_api_project.entity.Todo;
import com.selcukaloba.to_do_api_project.repository.TodoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TodoServiceImpl implements ITodoService{

    @Autowired
    private TodoRepository todoRepository;

    @Override
    public TodoResponse createTodo(TodoCreateRequest request) {
        TodoResponse response = new TodoResponse();
        Todo todo = new Todo();
        BeanUtils.copyProperties(request, todo);
        Todo dbTodo = todoRepository.save(todo);
        BeanUtils.copyProperties(dbTodo, response);
        return response;
    }

    @Override
    public List<TodoResponse> getAllTodo() {
        List<Todo> todoList = todoRepository.findAll();
        List<TodoResponse> responseList = new ArrayList<>();
        for(Todo todo: todoList)
        {
            TodoResponse dto = new TodoResponse();
            BeanUtils.copyProperties(todo, dto);
            responseList.add(dto);
        }
        return responseList;
    }

    @Override
    public TodoResponse updateTodo(Long id, TodoUpdateRequest request) {
        Todo todo = todoRepository.findById(id).orElseThrow(()->new RuntimeException("Todo not found with id of "+ id));

            BeanUtils.copyProperties(request, todo); //postmanden geleni db'dekinin üstüne yaz
            todo.setId(id);//id kopyalanırken bozulmasın
            Todo updatedTodo = todoRepository.save(todo);
            TodoResponse response = new TodoResponse();
            BeanUtils.copyProperties(updatedTodo, response); //gönderme dtosuna çevir
            return response;
    }

    @Override
    public void deleteTodo(Long id) {
        if(todoRepository.existsById(id))
        {
            todoRepository.deleteById(id);
        }
        else
        {
            throw new RuntimeException("Todo not found with id of " + id);
        }
    }

    @Override
    public List<TodoResponse> getUpcomingReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetTime = now.plusDays(7);

        List<Todo> todoList = todoRepository.findByIsCompletedFalseAndReminderDateBetween(now, targetTime);
        List<TodoResponse> responseList = new ArrayList<>();

        for(Todo todo : todoList)
        {
            TodoResponse dto = new TodoResponse();
            BeanUtils.copyProperties(todo, dto);
            responseList.add(dto);
        }
        return responseList;
    }
}
