package com.selcukaloba.to_do_api_project.controller.web;

import com.selcukaloba.to_do_api_project.dto.ApiTeamResponse;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoCreateRequest;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoResponse;
import com.selcukaloba.to_do_api_project.service.IFriendRequestService;
import com.selcukaloba.to_do_api_project.service.ITeamService;
import com.selcukaloba.to_do_api_project.service.ITodoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class TeamController {

    @Autowired
    private ITeamService teamService;

    @Autowired
    private IFriendRequestService friendRequestService;

    @Autowired
    private ITodoService todoService;

    @GetMapping("/teams")
    public String showTeamsPage(Model model, Principal principal) {
        String username = principal.getName();
        List<ApiTeamResponse> teams = teamService.getTeams(username);

        model.addAttribute("teams", teams);
        model.addAttribute("friends", friendRequestService.getAllFriends(username));
        model.addAttribute("myTodos", todoService.getAllTodo(username).stream()
                .filter(t -> t.getIsCompleted() == null || !t.getIsCompleted())
                .toList());
        model.addAttribute("username", username);
        return "teams";
    }

    @PostMapping("/teams/create")
    public String createTeam(@RequestParam String teamName, Principal principal, RedirectAttributes redirectAttributes) {
        teamService.createTeam(teamName, principal.getName());
        redirectAttributes.addFlashAttribute("successMsg", "Team created successfully!");
        return "redirect:/teams";
    }

    @PostMapping("/teams/{teamId}/add-member")
    public String addMember(@PathVariable Long teamId, @RequestParam String memberUsername, Principal principal, RedirectAttributes redirectAttributes) {
        teamService.addMember(teamId, memberUsername, principal.getName());
        redirectAttributes.addFlashAttribute("successMsg", "Member added successfully!");
        return "redirect:/teams";
    }

    @PostMapping("/teams/{teamId}/remove-member/{memberUsername}")
    public String removeMember(@PathVariable Long teamId, @PathVariable String memberUsername, Principal principal, RedirectAttributes redirectAttributes) {
        teamService.removeMember(teamId, memberUsername, principal.getName());
        redirectAttributes.addFlashAttribute("successMsg", "Member removed successfully!");
        return "redirect:/teams";
    }

    @PostMapping("/teams/{teamId}/assign-todo")
    public String assignTodoToTeam(@PathVariable Long teamId,
                                   @Valid @ModelAttribute ApiTodoCreateRequest request,
                                   BindingResult bindingResult,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMsg", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/teams";
        }
        request.setTeamId(teamId);
        teamService.assignTodoToTeam(request, principal.getName());
        redirectAttributes.addFlashAttribute("successMsg", "Task assigned successfully!");
        return "redirect:/teams";
    }

    @PostMapping("/teams/{teamId}/assign-existing-todo")
    public String assignExistingTodoToTeam(@PathVariable Long teamId,
                                           @RequestParam Long todoId,
                                           @RequestParam(required = false) String assignedToUsername,
                                           Principal principal,
                                           RedirectAttributes redirectAttributes) {
        teamService.assignExistingTodoToTeam(teamId, todoId, assignedToUsername, principal.getName());
        redirectAttributes.addFlashAttribute("successMsg", "Task assigned to team!");
        return "redirect:/teams";
    }

    @PostMapping("/teams/todo/delete/{todoId}")
    public String deleteTeamTodo(@PathVariable Long todoId, Principal principal, RedirectAttributes redirectAttributes) {
        teamService.deleteTeamTodo(todoId, principal.getName());
        redirectAttributes.addFlashAttribute("successMsg", "Task deleted successfully!");
        return "redirect:/teams";
    }

    @GetMapping("/teams/{teamId}/tasks")
    public String showTeamTasks(@PathVariable Long teamId, Model model, Principal principal) {
        List<ApiTodoResponse> tasks = teamService.getTeamTodos(teamId);
        model.addAttribute("tasks", tasks);
        model.addAttribute("team", teamService.getTeamDetail(teamId));
        model.addAttribute("username", principal.getName());
        return "team-tasks";
    }

    @PostMapping("/teams/{teamId}/delete")
    public String deleteTeam(@PathVariable Long teamId, Principal principal, RedirectAttributes redirectAttributes) {
        teamService.deleteTeam(teamId, principal.getName());
        redirectAttributes.addFlashAttribute("successMsg", "Team deleted successfully!");
        return "redirect:/teams";
    }

    @PostMapping("/teams/{teamId}/leave")
    public String leaveTeam(@PathVariable Long teamId, Principal principal, RedirectAttributes redirectAttributes) {
        teamService.leaveTeam(teamId, principal.getName());
        redirectAttributes.addFlashAttribute("successMsg", "You left the team!");
        return "redirect:/teams";
    }
}
