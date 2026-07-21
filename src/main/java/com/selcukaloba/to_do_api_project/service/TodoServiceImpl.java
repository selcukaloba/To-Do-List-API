package com.selcukaloba.to_do_api_project.service;

import com.selcukaloba.to_do_api_project.dto.todo.TodoCreateRequest;
import com.selcukaloba.to_do_api_project.dto.todo.TodoResponse;
import com.selcukaloba.to_do_api_project.dto.todo.TodoShareRequestResponse;
import com.selcukaloba.to_do_api_project.dto.todo.TodoUpdateRequest;
import com.selcukaloba.to_do_api_project.entity.Todo;
import com.selcukaloba.to_do_api_project.entity.TodoShare;
import com.selcukaloba.to_do_api_project.entity.TodoShareRequest;
import com.selcukaloba.to_do_api_project.entity.User;
import com.selcukaloba.to_do_api_project.enums.TodoShareStatus;
import com.selcukaloba.to_do_api_project.exception.BaseException;
import com.selcukaloba.to_do_api_project.exception.ErrorMessage;
import com.selcukaloba.to_do_api_project.exception.MessageType;
import com.selcukaloba.to_do_api_project.repository.TodoRepository;
import com.selcukaloba.to_do_api_project.repository.TodoShareRepository;
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
    @Autowired
    private TodoShareRepository todoShareRepository;

    @Override
    public TodoResponse createTodo(TodoCreateRequest request, String username) {
        User owner = userRepository.findByUsername(username).orElseThrow(()->new BaseException(new ErrorMessage("Username: " + username, MessageType.USERNAME_NOT_FOUND)));

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
                .orElseThrow(() -> new BaseException(new ErrorMessage("Todo ID: " + id, MessageType.NO_RECORD_EXISTS)));
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
            throw new BaseException(new ErrorMessage("Todo ID: " + id, MessageType.NO_RECORD_EXISTS));
        }
    }

    @Value("${todo.max.upcoming.days:30}")
    private int maxUpcomingDays;

    @Override
    public List<TodoResponse> getUpcomingReminders(String username, int days) {

        if(days<0 || days>maxUpcomingDays)
        {
            throw new BaseException(new ErrorMessage("Requested days: "+ days + ", Max allowed: "+ maxUpcomingDays, MessageType.INVALID_DAY_RANGE));
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetTime = now.plusDays(days);

        List<Todo> todoList = todoRepository.findUpcomingRemindersByUser(now, targetTime, username);
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
        Todo todo = todoRepository.findById(id).orElseThrow(()->new BaseException(new ErrorMessage("Todo ID: "+ id, MessageType.TODO_NOT_FOUND)));

        if(!ownerUsername.equals(todo.getUser().getUsername()))
        {
            throw new BaseException(new ErrorMessage("User " + ownerUsername + " is not owner of Todo ID: " +  id, MessageType.NOT_TODO_OWNER));
        }

        User friend = userRepository.findByUsername(friendUsername).orElseThrow(()-> new BaseException(new ErrorMessage("Friend name: "+ friendUsername, MessageType.FRIEND_NOT_FOUND)));

        if(!todo.getUser().getFriends().contains(friend))
        {
            throw new BaseException(new ErrorMessage("Target: " + friendUsername, MessageType.NOT_FRIENDS));
        }

        if(todoShareRepository.existsByTodoAndSharedUser(todo, friend))
        {
            throw new BaseException(new ErrorMessage("Todo ID: "+ id + " already shared with" + friendUsername, MessageType.ALREADY_SHARED));
        }

        boolean hasPendingShare = todoShareRequestRepository
                .findByReceiverUsernameAndStatus(friendUsername, TodoShareStatus.PENDING)
                .stream()
                .anyMatch(req -> req.getTodo().getId().equals(id) && req.getSender().getUsername().equals(ownerUsername));

        if (hasPendingShare) {
            throw new BaseException(new ErrorMessage("Pending request is existed for: " + friendUsername, MessageType.SHARE_REQUEST_PENDING));
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
        List<Todo> todos = todoRepository.findAllTodosByOwnerOrSharedUser(username);

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
        TodoShareRequest todoShareRequest = todoShareRequestRepository.findById(requestId).orElseThrow(()->new BaseException(new ErrorMessage("Request ID: " + requestId, MessageType.SHARE_REQUEST_NOT_FOUND)));
        Todo todo = todoShareRequest.getTodo();
        User receiver = todoShareRequest.getReceiver();
        User sender = todoShareRequest.getSender();

        TodoShare sharedTodo = new TodoShare();
        sharedTodo.setTodo(todo);
        sharedTodo.setSharedUser(receiver);
        sharedTodo.setSharedBy(sender);

        todoShareRepository.save(sharedTodo);
        todoShareRequestRepository.delete(todoShareRequest);
    }

    @Override
    public void rejectShareRequest(Long requestId) {
        TodoShareRequest todoShareRequest = todoShareRequestRepository.findById(requestId).orElseThrow(()->new BaseException(new ErrorMessage("Request ID: " + requestId, MessageType.SHARE_REQUEST_NOT_FOUND)));
        todoShareRequestRepository.delete(todoShareRequest);
    }
}
