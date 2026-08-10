package com.selcukaloba.to_do_api_project.service;

import com.selcukaloba.to_do_api_project.dto.ApiCommentCreateRequest;
import com.selcukaloba.to_do_api_project.dto.ApiCommentResponse;
import com.selcukaloba.to_do_api_project.entity.Comment;
import com.selcukaloba.to_do_api_project.entity.Todo;
import com.selcukaloba.to_do_api_project.entity.TodoShare;
import com.selcukaloba.to_do_api_project.entity.User;
import com.selcukaloba.to_do_api_project.exception.BaseException;
import com.selcukaloba.to_do_api_project.exception.ErrorMessage;
import com.selcukaloba.to_do_api_project.exception.MessageType;
import com.selcukaloba.to_do_api_project.repository.CommentRepository;
import com.selcukaloba.to_do_api_project.repository.TodoRepository;
import com.selcukaloba.to_do_api_project.repository.TodoShareRepository;
import com.selcukaloba.to_do_api_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements ICommentService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private TodoShareRepository todoShareRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    @Transactional
    public ApiCommentResponse addComment(Long todoId, String username, String content) {
        User user = userRepository.findByUsername(username).orElseThrow(()->new BaseException(new ErrorMessage(username, MessageType.USERNAME_NOT_FOUND)));
        Todo todo = todoRepository.findById(todoId).orElseThrow(()->new BaseException(new ErrorMessage("Todo ID:" + todoId, MessageType.TODO_NOT_FOUND)));

        boolean isOwner = todo.getUser().getUsername().equals(username);
        boolean isShared = todoShareRepository.existsByTodoIdAndSharedUserId(todoId, user.getId());

        if (!isOwner && !isShared)
        {
            throw new BaseException(new ErrorMessage(username, MessageType.NOT_TODO_OWNER));
        }

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUser(user);
        comment.setTodo(todo);
        comment = commentRepository.save(comment);

        return mapToResponse(comment);
    }

    @Override
    public List<ApiCommentResponse> getComments(Long todoId) {
        return commentRepository.findByTodoIdOrderByCreatedAtAsc(todoId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ApiCommentResponse mapToResponse(Comment comment) {
        ApiCommentResponse response = new ApiCommentResponse();
        response.setContent(comment.getContent());
        response.setUsername(comment.getUser().getUsername());
        response.setCreatedAt(comment.getCreatedAt());
        response.setId(comment.getId());
        return response;
    }
}
