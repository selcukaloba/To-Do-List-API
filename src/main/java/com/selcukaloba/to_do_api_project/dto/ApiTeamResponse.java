package com.selcukaloba.to_do_api_project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiTeamResponse {
    private Long id;
    private String name;
    private String leaderUsername;
    private LocalDateTime createdAt;
    private List<ApiUserResponse> members;
}
