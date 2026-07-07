package com.selcukaloba.to_do_api_project.service;

import com.selcukaloba.to_do_api_project.dto.TodoCreateRequest;
import com.selcukaloba.to_do_api_project.dto.TodoResponse;
import com.selcukaloba.to_do_api_project.dto.TodoUpdateRequest;
import com.selcukaloba.to_do_api_project.entity.Todo;
import com.selcukaloba.to_do_api_project.exception.BaseException;
import com.selcukaloba.to_do_api_project.exception.ErrorMessage;
import com.selcukaloba.to_do_api_project.exception.MessageType;
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
        List<Todo> todoList = todoRepository.findByIsCompletedFalse();
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
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage("id: " + id, MessageType.NO_RECORD_EXISTS)));
            BeanUtils.copyProperties(request, todo);
            todo.setId(id);//id kopyalanırken bozulmasın
            Todo updatedTodo = todoRepository.save(todo);
            TodoResponse response = new TodoResponse();
            BeanUtils.copyProperties(updatedTodo, response);
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
            throw new BaseException(new ErrorMessage("id: " + id, MessageType.NO_RECORD_EXISTS));
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
