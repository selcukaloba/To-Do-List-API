package com.selcukaloba.to_do_api_project.service;

import com.selcukaloba.to_do_api_project.dto.ApiTeamResponse;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoCreateRequest;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoResponse;

import java.util.List;

public interface ITeamService {
    ApiTeamResponse createTeam(String name, String leaderUsername);
    void addMember(Long teamId, String memberUsername, String leaderUsername);
    void removeMember(Long teamId, String memberUsername, String leaderUsername);
    List<ApiTeamResponse> getTeams(String username);
    ApiTeamResponse getTeamDetail(Long teamId);
    List<ApiTodoResponse> getTeamTodos(Long teamId);
    ApiTodoResponse assignTodoToTeam(ApiTodoCreateRequest request, String leaderUsername);
    void deleteTeamTodo(Long todoId, String leaderUsername);
    void assignExistingTodoToTeam(Long teamId, Long todoId, String assignedToUsername, String leaderUsername);
    List<ApiTodoResponse> getTeamTodosByMonth(Long teamId, int year, int month);
}
