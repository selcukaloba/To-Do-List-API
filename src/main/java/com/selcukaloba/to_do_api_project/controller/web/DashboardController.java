package com.selcukaloba.to_do_api_project.controller.web;

import com.selcukaloba.to_do_api_project.dto.ApiTeamResponse;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoCreateRequest;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoResponse;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoUpdateRequest;
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
import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private ITodoService todoService;

    @Autowired
    private ITeamService teamService;

    @GetMapping("/dashboard")
    public String showDashboardPage(@RequestParam(value = "days", required = false) Integer days,
                                    @RequestParam(value = "view", required = false) String view,
                                    @RequestParam(value = "teamId", required = false) Long teamId,
                                    @RequestParam(value = "month", required = false) String month,
                                    Model model, Principal principal) {
        String username = principal.getName();
        model.addAttribute("teams", teamService.getTeams(username));
        model.addAttribute("username", username);

        if ("team".equals(view)) {
            model.addAttribute("view", "team");
            model.addAttribute("newTodo", new ApiTodoCreateRequest());

            if (teamId != null) {
                if (!teamService.isUserTeamMember(teamId, username)) {
                    model.addAttribute("view", "team");
                    model.addAttribute("errorMsg", "You are not a member of this team!");
                    return "dashboard";
                }

                int year = java.time.Year.now().getValue();
                int monthValue = java.time.LocalDate.now().getMonthValue();

                if (month != null && month.matches("\\d{4}-\\d{2}")) {
                    String[] parts = month.split("-");
                    year = Integer.parseInt(parts[0]);
                    monthValue = Integer.parseInt(parts[1]);
                }

                Map<String, Object> calendarData = teamService.buildCalendarData(year, monthValue, teamId);
                ApiTeamResponse selectedTeam = teamService.getTeamDetail(teamId);

                model.addAttribute("dayHeaders", calendarData.get("dayHeaders"));
                model.addAttribute("monthLabel", calendarData.get("monthLabel"));
                model.addAttribute("prevMonth", calendarData.get("prevMonth"));
                model.addAttribute("nextMonth", calendarData.get("nextMonth"));
                model.addAttribute("calendarData", calendarData.get("calendar"));
                model.addAttribute("selectedTeam", selectedTeam);
            }

            return "dashboard";
        }

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

    @PostMapping("/dashboard/todo/create")
    public String createTodo(@Valid @ModelAttribute ApiTodoCreateRequest request,
                             BindingResult bindingResult,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMsg", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/dashboard";
        }
        todoService.createTodo(request, principal.getName());
        redirectAttributes.addFlashAttribute("successMsg", "Task created successfully!");
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/todo/update/{id}")
    public String updateTodo(@PathVariable Long id,
                             @Valid @ModelAttribute ApiTodoUpdateRequest request,
                             BindingResult bindingResult,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMsg", bindingResult.getAllErrors().get(0).getDefaultMessage());
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
}
