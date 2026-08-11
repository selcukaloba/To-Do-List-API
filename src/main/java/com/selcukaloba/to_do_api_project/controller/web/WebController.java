package com.selcukaloba.to_do_api_project.controller.web;

import com.selcukaloba.to_do_api_project.dto.*;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoCreateRequest;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoResponse;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoShareRequestResponse;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoUpdateRequest;
import com.selcukaloba.to_do_api_project.exception.BaseException;
import com.selcukaloba.to_do_api_project.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class WebController {

    @Autowired
    private IAuthService authService;

    @Autowired
    private ITodoService todoService;

    @Autowired
    private IFriendRequestService friendRequestService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ITeamService teamService;

    @Autowired
    private ICommentService commentService;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute ApiRegisterRequest registerRequest) {
        authService.register(registerRequest);
        return "redirect:/login?registered=true";
    }

    @GetMapping("/dashboard")
    public String showDashboardPage(@RequestParam(value = "days", required = false) Integer days,
                                    @RequestParam(value = "view", required = false) String view,
                                    @RequestParam(value = "teamId", required = false) Long teamId,
                                    @RequestParam(value = "month", required = false) String month,
                                    Model model, Principal principal) {
        String username = principal.getName();

        model.addAttribute("teams", teamService.getTeams(username));
        model.addAttribute("username", username);

        // SADECE "team" şartını arıyoruz. teamId null olsa bile buraya girmeli.
        if ("team".equals(view)) {
            model.addAttribute("view", "team");
            model.addAttribute("newTodo", new ApiTodoCreateRequest());

            // Eğer kullanıcı dropdown'dan bir takım seçmişse (teamId null değilse) takvimi doldur
            if (teamId != null) {
                int year = java.time.Year.now().getValue();
                int monthValue = java.time.LocalDate.now().getMonthValue();

                if (month != null && month.matches("\\d{4}-\\d{2}")) {
                    String[] parts = month.split("-");
                    year = Integer.parseInt(parts[0]);
                    monthValue = Integer.parseInt(parts[1]);
                }

                List<ApiTodoResponse> teamTodos = teamService.getTeamTodosByMonth(teamId, year, monthValue);
                ApiTeamResponse selectedTeam = teamService.getTeamDetail(teamId);

                model.addAttribute("dayHeaders", List.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"));

                String monthName = java.time.Month.of(monthValue).name();
                monthName = monthName.charAt(0) + monthName.substring(1).toLowerCase();
                model.addAttribute("monthLabel", monthName + " " + year);

                int prevYear = (monthValue == 1) ? year - 1 : year;
                int prevMonth = (monthValue == 1) ? 12 : monthValue - 1;
                int nextYear = (monthValue == 12) ? year + 1 : year;
                int nextMonth = (monthValue == 12) ? 1 : monthValue + 1;

                model.addAttribute("prevMonth", String.format("%d-%02d", prevYear, prevMonth));
                model.addAttribute("nextMonth", String.format("%d-%02d", nextYear, nextMonth));
                model.addAttribute("calendarData", buildCalendarData(year, monthValue, teamTodos));
                model.addAttribute("selectedTeam", selectedTeam);
                model.addAttribute("teamTodos", teamTodos);
            }

            return "dashboard";
        }

        // --- WORKSPACE (VARSAYILAN) GÖRÜNÜMÜ ---
        List<ApiTodoResponse> todos;
        if (days != null) {
            todos = todoService.getUpcomingReminders(username, days);
        } else {
            todos = todoService.getAllTodo(username);
        }

        model.addAttribute("todos", todos);
        model.addAttribute("newTodo", new ApiTodoCreateRequest());
        model.addAttribute("view", "workspace");

        return "dashboard";
    }

    private List<Map<String, Object>> buildCalendarData(int year, int month, List<ApiTodoResponse> teamTodos) {
        List<Map<String, Object>> calendar = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate firstDay = LocalDate.of(year, month, 1);
        int daysInMonth = firstDay.lengthOfMonth();

        int startDayOfWeek = firstDay.getDayOfWeek().getValue();
        for (int i = 1; i < startDayOfWeek; i++) {
            Map<String, Object> emptyDay = new HashMap<>();
            emptyDay.put("day", null);
            emptyDay.put("currentMonth", false);
            emptyDay.put("today", false);
            emptyDay.put("todos", Collections.emptyList());
            calendar.add(emptyDay);
        }

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(year, month, day);
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("day", day);
            dayData.put("currentMonth", true);
            dayData.put("today", date.equals(today));

            List<ApiTodoResponse> dayTodos = teamTodos.stream()
                    .filter(t -> t.getDueDate() != null && t.getDueDate().toLocalDate().equals(date))
                    .collect(Collectors.toList());

            if (!dayTodos.isEmpty()) {
                System.out.println("  Day " + day + ": " + dayTodos.size() + " todos");
            }

            dayData.put("todos", dayTodos);
            calendar.add(dayData);
        }

        while (calendar.size() % 7 != 0) {
            Map<String, Object> emptyDay = new HashMap<>();
            emptyDay.put("day", null);
            emptyDay.put("currentMonth", false);
            emptyDay.put("today", false);
            emptyDay.put("todos", Collections.emptyList());
            calendar.add(emptyDay);
        }

        return calendar;
    }

    @PostMapping("/dashboard/todo/create")
    public String createTodo(@Valid @ModelAttribute ApiTodoCreateRequest request, BindingResult bindingResult, Principal principal, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            String validationError = bindingResult.getAllErrors().get(0).getDefaultMessage();
            redirectAttributes.addFlashAttribute("errorMsg", validationError);
            return "redirect:/dashboard";
        }

        todoService.createTodo(request, principal.getName());
        redirectAttributes.addFlashAttribute("successMsg", "Task created successfully!");
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/todo/update/{id}")
    public String updateTodo(@PathVariable Long id, @Valid @ModelAttribute ApiTodoUpdateRequest request, BindingResult bindingResult, Principal principal, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            String validationError = bindingResult.getAllErrors().get(0).getDefaultMessage();
            redirectAttributes.addFlashAttribute("errorMsg", validationError);
            return "redirect:/dashboard";
        }

        todoService.updateTodo(id, request, principal.getName());
        redirectAttributes.addFlashAttribute("successMsg", "Task updated successfully!");
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/todo/delete/{id}")
    public String deleteTodo(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        todoService.deleteTodo(id, principal.getName());
        redirectAttributes.addFlashAttribute("successMsg", "Task deleted successfully!");
        return "redirect:/dashboard";
    }

    @GetMapping("/friends")
    public String showFriendsPage(Model model, Principal principal) {
        String username = principal.getName();

        List<ApiFriendRequestResponse> pendingRequests = friendRequestService.getPendingRequests(username);
        List<ApiUserResponse> friends = friendRequestService.getAllFriends(username);

        List<ApiTodoResponse> myTodos = todoService.getAllTodo(username).stream()
                .filter(t -> t.getIsCompleted() == null || !t.getIsCompleted())
                .toList();

        List<ApiTodoShareRequestResponse> pendingShareRequests = todoService.getPendingShareRequests(username);

        model.addAttribute("pendingRequests", pendingRequests != null ? pendingRequests : Collections.emptyList());
        model.addAttribute("friends", friends != null ? friends : Collections.emptyList());
        model.addAttribute("myTodos", myTodos != null ? myTodos : Collections.emptyList());
        model.addAttribute("pendingShareRequests", pendingShareRequests != null ? pendingShareRequests : Collections.emptyList());

        return "friends";
    }

    @PostMapping("/friends/request/send")
    public String sendFriendRequest(@RequestParam String receiverUsername, Principal principal, RedirectAttributes redirectAttributes) {
        friendRequestService.sendFriendRequest(principal.getName(), receiverUsername);
        redirectAttributes.addFlashAttribute("successMsg", "Friend request sent!");
        return "redirect:/friends";
    }

    @PostMapping("/friends/request/accept/{id}")
    public String acceptFriendRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        friendRequestService.acceptRequest(id);
        redirectAttributes.addFlashAttribute("successMsg", "Friend request accepted!");
        return "redirect:/friends";
    }

    @PostMapping("/friends/request/delete/{id}")
    public String deleteFriendRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        friendRequestService.deleteRequest(id);
        redirectAttributes.addFlashAttribute("successMsg", "Friend request rejected!");
        return "redirect:/friends";
    }

    @PostMapping("/friends/share")
    public String shareTodo(@RequestParam Long todoId, @RequestParam String friendUsername, Principal principal, RedirectAttributes redirectAttributes) {
        todoService.shareTodoWithFriend(todoId, principal.getName(), friendUsername);
        redirectAttributes.addFlashAttribute("successMsg", "Task invitation sent successfully!");
        return "redirect:/friends";
    }

    @PostMapping("/friends/share/accept/{id}")
    public String acceptShareRequest(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        todoService.acceptShareRequest(id, principal.getName());
        redirectAttributes.addFlashAttribute("successMsg", "Shared task accepted!");
        return "redirect:/friends";
    }

    @PostMapping("/friends/share/reject/{id}")
    public String rejectShareRequest(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        todoService.rejectShareRequest(id, principal.getName());
        redirectAttributes.addFlashAttribute("successMsg", "Shared task rejected!");
        return "redirect:/friends";
    }

    @PostMapping("/friends/unfriend/{friendUsername}")
    public String unfriend(@PathVariable String friendUsername, @RequestParam(defaultValue = "false") boolean keepShared, Principal principal, RedirectAttributes redirectAttributes) {
        friendRequestService.unfriend(principal.getName(), friendUsername, keepShared);
        redirectAttributes.addFlashAttribute("successMsg", "Unfriend successfuly!");
        return "redirect:/friends";
    }

    @GetMapping("/teams")
    public String showTeamsPage(Model model, Principal principal) {
        String username = principal.getName();

        List<ApiTeamResponse> teams = teamService.getTeams(username);
        System.out.println("=== " + username + " teams count: " + teams.size());
        for (ApiTeamResponse t : teams) {
            System.out.println("Team: " + t.getName() + " | Leader: " + t.getLeaderUsername());
        }

        model.addAttribute("teams", teamService.getTeams(username));
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
    public String addMember(@PathVariable long teamId, @RequestParam String memberUsername, Principal principal, RedirectAttributes redirectAttributes) {
        teamService.addMember(teamId, memberUsername, principal.getName());
        redirectAttributes.addFlashAttribute("successMsg", "Member added successfuly!");
        return "redirect:/teams";
    }

    @PostMapping("teams/{teamId}/remove-member/{memberUsername}")
    public String removeMember(@PathVariable long teamId, @PathVariable String memberUsername, Principal principal, RedirectAttributes redirectAttributes) {
        teamService.removeMember(teamId, memberUsername, principal.getName());
        redirectAttributes.addFlashAttribute("successMsg", "Removed successfully!");
        return "redirect:/teams";
    }

    @PostMapping("teams/{teamId}/assign-todo")
    public String assignTodoToTeam(@PathVariable long teamId, @Valid @ModelAttribute ApiTodoCreateRequest request, BindingResult bindingResult, Principal principal, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMsg", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/teams";
        }
        request.setTeamId(teamId);
        teamService.assignTodoToTeam(request, principal.getName());
        redirectAttributes.addFlashAttribute("successMsg", "Assigned successfully!");
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

    @PostMapping("teams/todo/delete/{todoId}")
    public String deleteTeamTodo(@PathVariable long todoId, Principal principal, RedirectAttributes redirectAttributes)
    {
        teamService.deleteTeamTodo(todoId, principal.getName());
        redirectAttributes.addFlashAttribute("successMsg", "Deleted successfully!");
        return "redirect:/teams";
    }

    @PostMapping("/dashboard/todo/{todoId}/comment")
    public String addComment(@PathVariable Long todoId, @RequestParam String content, Principal principal, RedirectAttributes redirectAttributes)
    {
        commentService.addComment(todoId, principal.getName(), content);
        redirectAttributes.addFlashAttribute("successMsg", "Comment added!");
        return "redirect:/dashboard/todo/" + todoId + "/comments";
    }

    @GetMapping("/dashboard/todo/{todoId}/comments")
    public String showCommentsPage(@PathVariable Long todoId, Model model, Principal principal)
    {
        List<ApiCommentResponse> comments = commentService.getComments(todoId);
        model.addAttribute("comments", comments);
        model.addAttribute("todoId", todoId);
        model.addAttribute("username", principal.getName());
        return "comments";
    }

    // Show Tasks sayfası
    @GetMapping("/teams/{teamId}/tasks")
    public String showTeamTasks(@PathVariable Long teamId, Model model, Principal principal) {
        List<ApiTodoResponse> tasks = teamService.getTeamTodos(teamId);
        model.addAttribute("tasks", tasks);
        model.addAttribute("team", teamService.getTeamDetail(teamId));
        model.addAttribute("username", principal.getName());
        return "team-tasks";
    }

    // Delete Team
    @PostMapping("/teams/{teamId}/delete")
    public String deleteTeam(@PathVariable Long teamId, Principal principal, RedirectAttributes redirectAttributes) {
        teamService.deleteTeam(teamId, principal.getName());
        redirectAttributes.addFlashAttribute("successMsg", "Team deleted successfully!");
        return "redirect:/teams";
    }

    // Leave Team
    @PostMapping("/teams/{teamId}/leave")
    public String leaveTeam(@PathVariable Long teamId, Principal principal, RedirectAttributes redirectAttributes) {
        teamService.leaveTeam(teamId, principal.getName());
        redirectAttributes.addFlashAttribute("successMsg", "You left the team!");
        return "redirect:/teams";
    }


}

