package com.selcukaloba.to_do_api_project.service;

import com.selcukaloba.to_do_api_project.dto.TodoCreateRequest;
import com.selcukaloba.to_do_api_project.dto.TodoResponse;
import com.selcukaloba.to_do_api_project.dto.TodoUpdateRequest;
import com.selcukaloba.to_do_api_project.entity.Todo;
import com.selcukaloba.to_do_api_project.entity.User;
import com.selcukaloba.to_do_api_project.exception.BaseException;
import com.selcukaloba.to_do_api_project.exception.ErrorMessage;
import com.selcukaloba.to_do_api_project.exception.MessageType;
import com.selcukaloba.to_do_api_project.repository.TodoRepository;
import com.selcukaloba.to_do_api_project.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TodoServiceImpl implements ITodoService{

    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private UserRepository userRepository;

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

    @Value("${todo.max.upcoming.days}")
    private int maxUpcomingDays;

    @Override
    public List<TodoResponse> getUpcomingReminders(int days) {

        if(days<0 || days>maxUpcomingDays)
        {
            throw new BaseException(new ErrorMessage("requested days: "+ days + ", max allowed: "+ maxUpcomingDays, MessageType.INVALID_DAY_RANGE));
        }


        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetTime = now.plusDays(days);

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

    @Override
    public void shareTodoWithFriend(Long id, String ownerUsername, String friendUsername) {
        Todo todo = todoRepository.findById(id).orElseThrow(()->new RuntimeException("Todo could not be found!"));

        if(!ownerUsername.equals(todo.getUser().getUsername()))
        {
            throw new RuntimeException("You are not the owner of this Todo!");
        }

        User friend = userRepository.findByUsername(friendUsername).orElseThrow(()-> new RuntimeException("Friend user could not be found!"));

        if(!todo.getUser().getFriends().contains(friend))
        {
            throw new RuntimeException("You can only share todo with your friends!");
        }

        if(todo.getSharedUsers().contains(friend))
        {
            throw new RuntimeException("Already shared!");
        }
        todo.getSharedUsers().add(friend);
        todoRepository.save(todo);

    }

    @Override
    public List<TodoResponse> getSharedTodos(String username) {
        List<Todo> todos = todoRepository.findByUserUsernameOrSharedUsersUsername(username, username);

        List<TodoResponse> responseList = new ArrayList<>();

        for(Todo todo : todos)
        {
            TodoResponse response = new TodoResponse();
            BeanUtils.copyProperties(todo, response);
            responseList.add(response);
        }
        return responseList;
    }
}
