package com.selcukaloba.to_do_api_project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiCommentResponse {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private String username;
}
