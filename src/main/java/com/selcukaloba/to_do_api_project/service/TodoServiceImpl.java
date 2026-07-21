package com.selcukaloba.to_do_api_project.service;

import com.selcukaloba.to_do_api_project.dto.todo.TodoCreateRequest;
import com.selcukaloba.to_do_api_project.dto.todo.TodoResponse;
import com.selcukaloba.to_do_api_project.dto.todo.TodoShareRequestResponse;
import com.selcukaloba.to_do_api_project.dto.todo.TodoUpdateRequest;
import com.selcukaloba.to_do_api_project.entity.Todo;
import com.selcukaloba.to_do_api_project.entity.TodoShareRequest;
import com.selcukaloba.to_do_api_project.entity.User;
import com.selcukaloba.to_do_api_project.enums.TodoShareStatus;
import com.selcukaloba.to_do_api_project.exception.BaseException;
import com.selcukaloba.to_do_api_project.exception.ErrorMessage;
import com.selcukaloba.to_do_api_project.exception.MessageType;
import com.selcukaloba.to_do_api_project.repository.TodoRepository;
import com.selcukaloba.to_do_api_project.repository.TodoShareRequestRepository;
import com.selcukaloba.to_do_api_project.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoServiceImpl implements ITodoService{

    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TodoShareRequestRepository todoShareRequestRepository;

    @Override
    public TodoResponse createTodo(TodoCreateRequest request, String username) {
        User owner = userRepository.findByUsername(username).orElseThrow(()->new RuntimeException("User could not be found!"));

        TodoResponse response = new TodoResponse();
        Todo todo = new Todo();
        BeanUtils.copyProperties(request, todo);
        todo.setUser(owner);
        Todo dbTodo = todoRepository.save(todo);
        BeanUtils.copyProperties(dbTodo, response);
        return response;
    }

    @Override
    public List<TodoResponse> getAllTodo(String username) {
        List<Todo> todoList = todoRepository.findAllTodosByOwnerOrSharedUser(username);
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

    @Value("${todo.max.upcoming.days:30}")
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

        boolean hasPendingShare = todoShareRequestRepository
                .findByReceiverUsernameAndStatus(friendUsername, TodoShareStatus.PENDING)
                .stream()
                .anyMatch(req -> req.getTodo().getId().equals(id) && req.getSender().getUsername().equals(ownerUsername));

        if (hasPendingShare) {
            throw new RuntimeException("A share request for this Todo is already pending!");
        }

        TodoShareRequest shareRequest = new TodoShareRequest();
        shareRequest.setTodo(todo);
        shareRequest.setSender(todo.getUser());
        shareRequest.setReceiver(friend);
        shareRequest.setStatus(TodoShareStatus.PENDING);
        todoShareRequestRepository.save(shareRequest);
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

    @Override
    public List<TodoShareRequestResponse> getPendingShareRequests(String username) {
        List<TodoShareRequest> requests = todoShareRequestRepository.findByReceiverUsernameAndStatus(username, TodoShareStatus.PENDING);
        return requests.stream()
                .map(req->new TodoShareRequestResponse(
                        req.getId(),
                        req.getSender().getUsername(),
                        req.getReceiver().getUsername(),
                        req.getStatus(),
                        req.getTodo().getId(),
                        req.getTodo().getTitle()
                )).collect(Collectors.toList());
    }

    @Override
    public void acceptShareRequest(Long requestId) {
        TodoShareRequest todoShareRequest = todoShareRequestRepository.findById(requestId).orElseThrow(()->new RuntimeException("Share request could not be found!"));
        Todo todo = todoShareRequest.getTodo();
        User receiver = todoShareRequest.getReceiver();
        todo.getSharedUsers().add(receiver);
        todoRepository.save(todo);
        todoShareRequestRepository.delete(todoShareRequest);
    }

    @Override
    public void rejectShareRequest(Long requestId) {
        TodoShareRequest todoShareRequest = todoShareRequestRepository.findById(requestId).orElseThrow(()->new RuntimeException("Share request   could not be found!"));
        todoShareRequestRepository.delete(todoShareRequest);
    }
}
