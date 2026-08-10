package com.selcukaloba.to_do_api_project.service;

import com.selcukaloba.to_do_api_project.dto.ApiCommentCreateRequest;
import com.selcukaloba.to_do_api_project.dto.ApiCommentResponse;

import java.util.List;

public interface ICommentService {
    ApiCommentResponse addComment(Long todoId, String username, String content);
    List<ApiCommentResponse > getComments(Long todoId);
}
