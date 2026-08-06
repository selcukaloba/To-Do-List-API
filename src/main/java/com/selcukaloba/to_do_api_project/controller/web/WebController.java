package com.selcukaloba.to_do_api_project.controller.web;

import com.selcukaloba.to_do_api_project.dto.ApiFriendRequestResponse;
import com.selcukaloba.to_do_api_project.dto.ApiRegisterRequest;
import com.selcukaloba.to_do_api_project.dto.ApiUserResponse;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoCreateRequest;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoResponse;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoShareRequestResponse;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoUpdateRequest;
import com.selcukaloba.to_do_api_project.exception.BaseException;
import com.selcukaloba.to_do_api_project.service.IAuthService;
import com.selcukaloba.to_do_api_project.service.IFriendRequestService;
import com.selcukaloba.to_do_api_project.service.ITodoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
    public String showDashboardPage(@RequestParam(value = "days", required = false) Integer days, Model model, Principal principal) {
        String username = principal.getName();
        List<ApiTodoResponse> todos;

        if (days != null) {
            todos = todoService.getUpcomingReminders(username, days);
        } else {
            todos = todoService.getAllTodo(username);
        }

        model.addAttribute("username", username);
        model.addAttribute("todos", todos);
        model.addAttribute("newTodo", new ApiTodoCreateRequest());
        return "dashboard";
    }

    @PostMapping("/dashboard/todo/create")
    public String createTodo(@Valid @ModelAttribute ApiTodoCreateRequest request,BindingResult bindingResult,Principal principal,RedirectAttributes redirectAttributes) {
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
    public String updateTodo(@PathVariable Long id,@Valid @ModelAttribute ApiTodoUpdateRequest request,BindingResult bindingResult,Principal principal,RedirectAttributes redirectAttributes) {
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
    public String sendFriendRequest(@RequestParam String receiverUsername, Principal principal,RedirectAttributes redirectAttributes) {
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
    public String shareTodo(@RequestParam Long todoId, @RequestParam String friendUsername,Principal principal, RedirectAttributes redirectAttributes) {
        todoService.shareTodoWithFriend(todoId, principal.getName(), friendUsername);
        redirectAttributes.addFlashAttribute("successMsg", "Task invitation sent successfully!");
        return "redirect:/friends";
    }

    @PostMapping("/friends/share/accept/{id}")
    public String acceptShareRequest(@PathVariable Long id, Principal principal,RedirectAttributes redirectAttributes) {
        todoService.acceptShareRequest(id, principal.getName());
        redirectAttributes.addFlashAttribute("successMsg", "Shared task accepted!");
        return "redirect:/friends";
    }

    @PostMapping("/friends/share/reject/{id}")
    public String rejectShareRequest(@PathVariable Long id, Principal principal,RedirectAttributes redirectAttributes) {
        todoService.rejectShareRequest(id, principal.getName());
        redirectAttributes.addFlashAttribute("successMsg", "Shared task rejected!");
        return "redirect:/friends";
    }

}