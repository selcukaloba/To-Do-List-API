package com.selcukaloba.to_do_api_project.service;

import com.selcukaloba.to_do_api_project.dto.ApiTeamResponse;
import com.selcukaloba.to_do_api_project.dto.ApiUserResponse;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoCreateRequest;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoResponse;
import com.selcukaloba.to_do_api_project.entity.Team;
import com.selcukaloba.to_do_api_project.entity.TeamMember;
import com.selcukaloba.to_do_api_project.entity.Todo;
import com.selcukaloba.to_do_api_project.entity.User;
import com.selcukaloba.to_do_api_project.exception.BaseException;
import com.selcukaloba.to_do_api_project.exception.ErrorMessage;
import com.selcukaloba.to_do_api_project.exception.MessageType;
import com.selcukaloba.to_do_api_project.repository.TeamMemberRepository;
import com.selcukaloba.to_do_api_project.repository.TeamRepository;
import com.selcukaloba.to_do_api_project.repository.TodoRepository;
import com.selcukaloba.to_do_api_project.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamServiceImpl implements ITeamService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Override
    @Transactional
    public ApiTeamResponse createTeam(String name, String leaderUsername) {
        User leader = userRepository.findByUsername(leaderUsername).orElseThrow(()->new BaseException(new ErrorMessage(leaderUsername, MessageType.USERNAME_NOT_FOUND)));
        if(teamRepository.existsByNameAndLeaderUsername(name, leaderUsername))
        {
            throw new BaseException(new ErrorMessage(name, MessageType.TEAM_ALREADY_EXISTS));
        }

        Team team = new Team();
        team.setName(name);
        team.setLeader(leader);
        team = teamRepository.save(team);

        return mapToTeamResponse(team);

    }

    @Override
    @Transactional
    public void addMember(Long teamId, String memberUsername, String leaderUsername) {
        Team team = teamRepository.findById(teamId).orElseThrow(()->new BaseException(new ErrorMessage("Team: " + teamId, MessageType.TEAM_NOT_FOUND)));
        User member = userRepository.findByUsername(memberUsername).orElseThrow(()->new BaseException(new ErrorMessage(memberUsername, MessageType.ALREADY_TEAM_MEMBER)));

        if (!team.getLeader().getUsername().equals(leaderUsername)) {
            throw new BaseException(new ErrorMessage(leaderUsername, MessageType.NOT_TEAM_LEADER));
        }

        if(!team.getLeader().getUsername().equals(leaderUsername))
        {
            throw new BaseException(new ErrorMessage(leaderUsername, MessageType.NOT_TEAM_LEADER));
        }
        if(teamMemberRepository.existsByTeamAndUser(team, member))
        {
            throw new BaseException(new ErrorMessage(leaderUsername, MessageType.ALREADY_TEAM_MEMBER));
        }

        TeamMember teamMember = new TeamMember();
        teamMember.setTeam(team);
        teamMember.setUser(member);
        teamMemberRepository.save(teamMember);
    }

    @Override
    public void removeMember(Long teamId, String memberUsername, String leaderUsername) {
        Team team = teamRepository.findById(teamId).orElseThrow(()->new BaseException(new ErrorMessage("Team ID: " + teamId, MessageType.TEAM_NOT_FOUND)));
        User member = userRepository.findByUsername(memberUsername).orElseThrow(()->new BaseException(new ErrorMessage(memberUsername, MessageType.USERNAME_NOT_FOUND)));
        if(!team.getLeader().getUsername().equals(leaderUsername))
        {
            throw new BaseException(new ErrorMessage(leaderUsername, MessageType.NOT_TEAM_LEADER));
        }
        TeamMember teamMember = teamMemberRepository.findByTeamAndUser(team, member).orElseThrow(()->new BaseException(new ErrorMessage(memberUsername, MessageType.NOT_TEAM_MEMBER)));
        teamMemberRepository.delete(teamMember);
    }

    @Override
    public List<ApiTeamResponse> getTeams(String username) {
        return teamRepository.findAllByUser(username).stream()
                .map(this::mapToTeamResponse)
                .collect(Collectors.toList());
    }


    @Override
    public ApiTeamResponse getTeamDetail(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BaseException(new ErrorMessage("Team: " + teamId, MessageType.TEAM_NOT_FOUND)));
        return mapToTeamResponse(team);
    }

    @Override
    public List<ApiTodoResponse> getTeamTodos(Long teamId) {
        return todoRepository.findByTeamId(teamId).stream()
                .map(todo -> {
                    ApiTodoResponse response = new ApiTodoResponse();
                    BeanUtils.copyProperties(todo, response);
                    if (todo.getUser() != null) response.setOwnerUsername(todo.getUser().getUsername());
                    if (todo.getTeam() != null) {
                        response.setTeamId(todo.getTeam().getId());
                        response.setTeamName(todo.getTeam().getName());
                    }
                    if (todo.getAssignedTo() != null) response.setAssignedToUsername(todo.getAssignedTo().getUsername());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApiTodoResponse assignTodoToTeam(ApiTodoCreateRequest request, String leaderUsername) {
        User leader = userRepository.findByUsername(leaderUsername).orElseThrow(()->new BaseException(new ErrorMessage(leaderUsername, MessageType.USERNAME_NOT_FOUND)));
        Team team = teamRepository.findById(request.getTeamId()).orElseThrow(()->new BaseException(new ErrorMessage("Team ID: " + request.getTeamId(), MessageType.TEAM_NOT_FOUND)));
        if(!team.getLeader().getUsername().equals(leaderUsername))
        {
            throw new BaseException(new ErrorMessage(leaderUsername, MessageType.NOT_TEAM_LEADER));
        }
        Todo todo = new Todo();
        BeanUtils.copyProperties(request, todo);
        todo.setUser(leader);
        todo.setTeam(team);
        if(request.getAssignedToUsername()!=null)
        {
            User assignedTo = userRepository.findByUsername(request.getAssignedToUsername())
                    .orElseThrow(()->new BaseException(new ErrorMessage(request.getAssignedToUsername(), MessageType.USERNAME_NOT_FOUND)));
            todo.setAssignedTo(assignedTo);
        }
        todo = todoRepository.save(todo);
        ApiTodoResponse response = new ApiTodoResponse();
        BeanUtils.copyProperties(todo, response);
        response.setOwnerUsername(leader.getUsername());
        response.setTeamId(team.getId());
        response.setTeamName(team.getName());
        if(todo.getAssignedTo()!=null)
        {
            response.setAssignedToUsername(todo.getAssignedTo().getUsername());
        }
        return response;
    }

    @Override
    @Transactional
    public void assignExistingTodoToTeam(Long teamId, Long todoId, String assignedToUsername, String leaderUsername) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BaseException(new ErrorMessage("Team: " + teamId, MessageType.TEAM_NOT_FOUND)));

        if (!team.getLeader().getUsername().equals(leaderUsername)) {
            throw new BaseException(new ErrorMessage(leaderUsername, MessageType.NOT_TEAM_LEADER));
        }

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(String.valueOf(todoId), MessageType.TODO_NOT_FOUND)));

        if (todo.getTeam() != null && todo.getTeam().getId().equals(teamId)) {
            throw new BaseException(new ErrorMessage(todo.getTitle(), MessageType.TODO_ALREADY_IN_TEAM));
        }

        todo.setTeam(team);

        if (assignedToUsername != null && !assignedToUsername.isEmpty()) {
            User assignedTo = userRepository.findByUsername(assignedToUsername)
                    .orElseThrow(() -> new BaseException(new ErrorMessage(assignedToUsername, MessageType.USERNAME_NOT_FOUND)));
            todo.setAssignedTo(assignedTo);
        }

        todoRepository.save(todo);
    }

    @Override
    @Transactional
    public void deleteTeamTodo(Long todoId, String leaderUsername) {
        Todo todo = todoRepository.findById(todoId).orElseThrow(()->new BaseException(new ErrorMessage(String.valueOf(todoId), MessageType.TODO_NOT_FOUND)));
        if(todo.getTeam()!=null && !todo.getTeam().getLeader().getUsername().equals(leaderUsername))
        {
            throw new BaseException(new ErrorMessage(leaderUsername, MessageType.NOT_TEAM_LEADER));
        }
        todoRepository.delete(todo);
    }

    private ApiTeamResponse mapToTeamResponse(Team team) {
        ApiTeamResponse response = new ApiTeamResponse();
        BeanUtils.copyProperties(team, response);
        response.setLeaderUsername(team.getLeader().getUsername());
        System.out.println("Team: " + team.getName() + ", Members count: " + team.getMembers().size());
        response.setMembers(team.getMembers().stream().map(tm -> {
            ApiUserResponse userDto = new ApiUserResponse();
            BeanUtils.copyProperties(tm.getUser(), userDto);
            return userDto;
        }).collect(Collectors.toList()));
        return response;
    }
}
