package com.selcukaloba.to_do_api_project.service.impl;

import com.selcukaloba.to_do_api_project.dto.ApiCommentResponse;
import com.selcukaloba.to_do_api_project.entity.Comment;
import com.selcukaloba.to_do_api_project.entity.Todo;
import com.selcukaloba.to_do_api_project.entity.User;
import com.selcukaloba.to_do_api_project.exception.BaseException;
import com.selcukaloba.to_do_api_project.exception.ErrorMessage;
import com.selcukaloba.to_do_api_project.exception.MessageType;
import com.selcukaloba.to_do_api_project.repository.*;
import com.selcukaloba.to_do_api_project.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements ICommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoShareRepository todoShareRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Override
    @Transactional
    public ApiCommentResponse addComment(Long todoId, String username, String content) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(new ErrorMessage(username, MessageType.USERNAME_NOT_FOUND)));

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new BaseException(new ErrorMessage("Todo ID: " + todoId, MessageType.TODO_NOT_FOUND)));

        // Yetki kontrolü
        checkAccess(todo, user);

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUser(user);
        comment.setTodo(todo);
        comment = commentRepository.save(comment);

        return mapToResponse(comment);
    }

    @Override
    public List<ApiCommentResponse> getComments(Long todoId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(new ErrorMessage(username, MessageType.USERNAME_NOT_FOUND)));

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new BaseException(new ErrorMessage("Todo ID: " + todoId, MessageType.TODO_NOT_FOUND)));

        // Yetki kontrolü - owner veya shared veya team member
        checkAccess(todo, user);

        return commentRepository.findByTodoIdOrderByCreatedAtAsc(todoId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void checkAccess(Todo todo, User user) {
        boolean isOwner = todo.getUser().getUsername().equals(user.getUsername());
        boolean isShared = todoShareRepository.existsByTodoIdAndSharedUserId(todo.getId(), user.getId());
        boolean isTeamMember = false;

        if (todo.getTeam() != null) {
            isTeamMember = teamMemberRepository.existsByTeamAndUser(todo.getTeam(), user);
        }

        if (!isOwner && !isShared && !isTeamMember) {
            throw new BaseException(new ErrorMessage(user.getUsername(), MessageType.NOT_TODO_OWNER));
        }
    }

    private ApiCommentResponse mapToResponse(Comment comment) {
        ApiCommentResponse response = new ApiCommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setUsername(comment.getUser().getUsername());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }
}