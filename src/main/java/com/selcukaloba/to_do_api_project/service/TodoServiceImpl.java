package com.selcukaloba.to_do_api_project.service;

import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoCreateRequest;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoResponse;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoShareRequestResponse;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoUpdateRequest;
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
import org.springframework.transaction.annotation.Transactional;

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
    public ApiTodoResponse createTodo(ApiTodoCreateRequest request, String username) {
        User owner = userRepository.findByUsername(username).orElseThrow(()->new BaseException(new ErrorMessage( username, MessageType.USERNAME_NOT_FOUND)));

        ApiTodoResponse response = new ApiTodoResponse();
        Todo todo = new Todo();
        BeanUtils.copyProperties(request, todo);
        todo.setUser(owner);
        Todo dbTodo = todoRepository.save(todo);
        BeanUtils.copyProperties(dbTodo, response);
        return response;
    }

    @Override
    public List<ApiTodoResponse> getAllTodo(String username) {
        List<Todo> todoList = todoRepository.findAllTodosByOwnerOrSharedUser(username);
        List<ApiTodoResponse> responseList = new ArrayList<>();
        for(Todo todo: todoList)
        {
            ApiTodoResponse dto = new ApiTodoResponse();
            BeanUtils.copyProperties(todo, dto);
            responseList.add(dto);
        }
        return responseList;
    }

    @Override
    public ApiTodoResponse updateTodo(Long id, ApiTodoUpdateRequest request, String username) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage("Todo ID: " + id, MessageType.NO_RECORD_EXISTS)));

        if(!todo.getUser().getUsername().equals(username))
        {
            throw new BaseException(new ErrorMessage("Todo ID: " + id, MessageType.NOT_TODO_OWNER));
        }

            BeanUtils.copyProperties(request, todo);
            todo.setId(id);//id kopyalanırken bozulmasın
            Todo updatedTodo = todoRepository.save(todo);
            ApiTodoResponse response = new ApiTodoResponse();
            BeanUtils.copyProperties(updatedTodo, response);
            return response;
    }

    @Override
    public void deleteTodo(Long id, String username) {
        Todo todo = todoRepository.findById(id).orElseThrow(()->new BaseException(new ErrorMessage("Todo ID: " + id, MessageType.NO_RECORD_EXISTS)));
        if(!todo.getUser().getUsername().equals(username))
        {
            throw new BaseException(new ErrorMessage("Todo ID: " + id, MessageType.NOT_TODO_OWNER));
        }
        todoRepository.delete(todo);
    }

    @Value("${todo.max.upcoming.days:30}")
    private int maxUpcomingDays;

    @Override
    public List<ApiTodoResponse> getUpcomingReminders(String username, int days) {

        if(days<0 || days>maxUpcomingDays)
        {
            throw new BaseException(new ErrorMessage("Requested: "+ days + ", Max: "+ maxUpcomingDays, MessageType.INVALID_DAY_RANGE));
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetTime = now.plusDays(days);

        List<Todo> todoList = todoRepository.findUpcomingRemindersByUser(now, targetTime, username);
        List<ApiTodoResponse> responseList = new ArrayList<>();

        for(Todo todo : todoList)
        {
            ApiTodoResponse dto = new ApiTodoResponse();
            BeanUtils.copyProperties(todo, dto);
            responseList.add(dto);
        }
        return responseList;
    }

    @Override
    @Transactional
    public void shareTodoWithFriend(Long id, String ownerUsername, String friendUsername) {
        Todo todo = todoRepository.findById(id).orElseThrow(()->new BaseException(new ErrorMessage("Todo ID: "+ id, MessageType.TODO_NOT_FOUND)));

        if(!ownerUsername.equals(todo.getUser().getUsername()))
        {
            throw new BaseException(new ErrorMessage("Todo ID: " +  id, MessageType.NOT_TODO_OWNER));
        }

        User friend = userRepository.findByUsername(friendUsername).orElseThrow(()-> new BaseException(new ErrorMessage(friendUsername, MessageType.FRIEND_NOT_FOUND)));

        if(!todo.getUser().getFriends().contains(friend))
        {
            throw new BaseException(new ErrorMessage(friendUsername, MessageType.NOT_FRIENDS));
        }

        if(todoShareRepository.existsByTodoAndSharedUser(todo, friend))
        {
            throw new BaseException(new ErrorMessage("Todo ID: "+ id + ", User: " + friendUsername, MessageType.ALREADY_SHARED));
        }

        boolean hasPendingShare = todoShareRequestRepository
                .findByReceiverUsernameAndStatus(friendUsername, TodoShareStatus.PENDING)
                .stream()
                .anyMatch(req -> req.getTodo().getId().equals(id) && req.getSender().getUsername().equals(ownerUsername));

        if (hasPendingShare) {
            throw new BaseException(new ErrorMessage(friendUsername, MessageType.SHARE_REQUEST_PENDING));
        }

        TodoShareRequest shareRequest = new TodoShareRequest();
        shareRequest.setTodo(todo);
        shareRequest.setSender(todo.getUser());
        shareRequest.setReceiver(friend);
        shareRequest.setStatus(TodoShareStatus.PENDING);
        todoShareRequestRepository.save(shareRequest);
    }

    @Override
    public List<ApiTodoResponse> getSharedTodos(String username) {
        List<Todo> todos = todoRepository.findAllTodosByOwnerOrSharedUser(username);

        List<ApiTodoResponse> responseList = new ArrayList<>();

        for(Todo todo : todos)
        {
            ApiTodoResponse response = new ApiTodoResponse();
            BeanUtils.copyProperties(todo, response);
            responseList.add(response);
        }
        return responseList;
    }

    @Override
    public List<ApiTodoShareRequestResponse> getPendingShareRequests(String username) {
        List<TodoShareRequest> requests = todoShareRequestRepository.findByReceiverUsernameAndStatus(username, TodoShareStatus.PENDING);
        return requests.stream()
                .map(req->new ApiTodoShareRequestResponse(
                        req.getId(),
                        req.getSender().getUsername(),
                        req.getReceiver().getUsername(),
                        req.getStatus(),
                        req.getTodo().getId(),
                        req.getTodo().getTitle()
                )).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void acceptShareRequest(Long requestId, String username) {
        TodoShareRequest todoShareRequest = todoShareRequestRepository.findById(requestId).orElseThrow(()->new BaseException(new ErrorMessage("Request ID: " + requestId, MessageType.SHARE_REQUEST_NOT_FOUND)));

        if(!todoShareRequest.getReceiver().getUsername().equals(username))
        {
            throw new BaseException(new ErrorMessage("Request ID: "+ requestId, MessageType.NOT_TODO_OWNER));
        }
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
    @Transactional
    public void rejectShareRequest(Long requestId, String username) {
        TodoShareRequest todoShareRequest = todoShareRequestRepository.findById(requestId).orElseThrow(()->new BaseException(new ErrorMessage("Request ID: " + requestId, MessageType.SHARE_REQUEST_NOT_FOUND)));
        if(!todoShareRequest.getReceiver().getUsername().equals(username))
        {
            throw new BaseException(new ErrorMessage("Request ID: " + requestId, MessageType.NOT_TODO_OWNER));
        }
        todoShareRequestRepository.delete(todoShareRequest);
    }
}
