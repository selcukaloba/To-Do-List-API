package com.selcukaloba.to_do_api_project.controller.web;

import com.selcukaloba.to_do_api_project.dto.ApiFriendRequestResponse;
import com.selcukaloba.to_do_api_project.dto.ApiRegisterRequest;
import com.selcukaloba.to_do_api_project.dto.ApiUserResponse;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoCreateRequest;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoResponse;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoShareRequestResponse;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoUpdateRequest;
import com.selcukaloba.to_do_api_project.exception.BaseException;
import com.selcukaloba.to_do_api_project.exception.ErrorMessage;
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

    private String getLocalizedMessage(BaseException ex, Locale locale) {
        ErrorMessage errorMessage = ex.getErrorMessage();
        String messageKey = errorMessage.getMessageType().getMessage();
        String localizedMessage = messageSource.getMessage(messageKey, null, messageKey, locale);
        if (errorMessage.getDetail() != null && !errorMessage.getDetail().isEmpty()) {
            localizedMessage += " : " + errorMessage.getDetail();
        }
        return localizedMessage;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute ApiRegisterRequest registerRequest, Model model) {
        try {
            authService.register(registerRequest);
            return "redirect:/login?registered=true";
        } catch (BaseException ex) {
            model.addAttribute("errorMsg", ex.getMessage());
            return "register";
        } catch (Exception ex) {
            model.addAttribute("errorMsg", "Registration failed. Please check your details.");
            return "register";
        }
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
    public String createTodo(@Valid @ModelAttribute ApiTodoCreateRequest request,
                             BindingResult bindingResult,
                             Principal principal,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        if (bindingResult.hasErrors()) {
            String validationError = bindingResult.getAllErrors().get(0).getDefaultMessage();
            redirectAttributes.addFlashAttribute("errorMsg", validationError);
            return "redirect:/dashboard";
        }

        try {
            todoService.createTodo(request, principal.getName());
            redirectAttributes.addFlashAttribute("successMsg", "Task created successfully!");
        } catch (BaseException ex) {
            redirectAttributes.addFlashAttribute("errorMsg", getLocalizedMessage(ex, locale));
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMsg", "Failed to create task.");
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/todo/update/{id}")
    public String updateTodo(@PathVariable Long id,
                             @Valid @ModelAttribute ApiTodoUpdateRequest request,
                             BindingResult bindingResult,
                             Principal principal,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        if (bindingResult.hasErrors()) {
            String validationError = bindingResult.getAllErrors().get(0).getDefaultMessage();
            redirectAttributes.addFlashAttribute("errorMsg", validationError);
            return "redirect:/dashboard";
        }

        try {
            todoService.updateTodo(id, request, principal.getName());
            redirectAttributes.addFlashAttribute("successMsg", "Task updated successfully!");
        } catch (BaseException ex) {
            redirectAttributes.addFlashAttribute("errorMsg", getLocalizedMessage(ex, locale));
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMsg", "Failed to update task.");
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/todo/delete/{id}")
    public String deleteTodo(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            todoService.deleteTodo(id, principal.getName());
            redirectAttributes.addFlashAttribute("successMsg", "Task deleted successfully!");
        } catch (BaseException ex) {
            redirectAttributes.addFlashAttribute("errorMsg", getLocalizedMessage(ex, locale));
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMsg", "Failed to delete task.");
        }
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
        List<ApiTodoResponse> sharedTodos = todoService.getSharedTodos(username).stream()
                .filter(t -> t.getOwnerUsername() != null && !t.getOwnerUsername().equals(username))
                .toList();

        List<ApiTodoShareRequestResponse> pendingShareRequests = todoService.getPendingShareRequests(username);

        model.addAttribute("pendingRequests", pendingRequests != null ? pendingRequests : Collections.emptyList());
        model.addAttribute("friends", friends != null ? friends : Collections.emptyList());
        model.addAttribute("myTodos", myTodos != null ? myTodos : Collections.emptyList());
        model.addAttribute("sharedTodos", sharedTodos);
        model.addAttribute("pendingShareRequests", pendingShareRequests != null ? pendingShareRequests : Collections.emptyList());

        return "friends";
    }

    @PostMapping("/friends/request/send")
    public String sendFriendRequest(@RequestParam String receiverUsername, Principal principal, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            friendRequestService.sendFriendRequest(principal.getName(), receiverUsername);
            redirectAttributes.addFlashAttribute("successMsg", "Friend request sent!");
        } catch (BaseException ex) {
            redirectAttributes.addFlashAttribute("errorMsg", getLocalizedMessage(ex, locale));
        }
        return "redirect:/friends";
    }

    @PostMapping("/friends/request/accept/{id}")
    public String acceptFriendRequest(@PathVariable Long id, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            friendRequestService.acceptRequest(id);
            redirectAttributes.addFlashAttribute("successMsg", "Friend request accepted!");
        } catch (BaseException ex) {
            redirectAttributes.addFlashAttribute("errorMsg", getLocalizedMessage(ex, locale));
        }
        return "redirect:/friends";
    }

    @PostMapping("/friends/request/delete/{id}")
    public String deleteFriendRequest(@PathVariable Long id, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            friendRequestService.deleteRequest(id);
            redirectAttributes.addFlashAttribute("successMsg", "Friend request rejected!");
        } catch (BaseException ex) {
            redirectAttributes.addFlashAttribute("errorMsg", getLocalizedMessage(ex, locale));
        }
        return "redirect:/friends";
    }

    @PostMapping("/friends/share")
    public String shareTodo(@RequestParam Long todoId,
                            @RequestParam String friendUsername,
                            Principal principal,
                            RedirectAttributes redirectAttributes,
                            Locale locale) {
        try {
            todoService.shareTodoWithFriend(todoId, principal.getName(), friendUsername);
            redirectAttributes.addFlashAttribute("successMsg", "Task invitation sent successfully!");
        } catch (BaseException ex) {
            redirectAttributes.addFlashAttribute("errorMsg", getLocalizedMessage(ex, locale));
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMsg", "Failed to share task. Please try again.");
        }
        return "redirect:/friends";
    }

    @PostMapping("/friends/share/accept/{id}")
    public String acceptShareRequest(@PathVariable Long id,
                                     Principal principal,
                                     RedirectAttributes redirectAttributes,
                                     Locale locale) {
        try {
            todoService.acceptShareRequest(id, principal.getName());
            redirectAttributes.addFlashAttribute("successMsg", "Shared task accepted!");
        } catch (BaseException ex) {
            redirectAttributes.addFlashAttribute("errorMsg", getLocalizedMessage(ex, locale));
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMsg", "An error occurred while accepting the task.");
        }
        return "redirect:/friends";
    }

    @PostMapping("/friends/share/reject/{id}")
    public String rejectShareRequest(@PathVariable Long id,
                                     Principal principal,
                                     RedirectAttributes redirectAttributes,
                                     Locale locale) {
        try {
            todoService.rejectShareRequest(id, principal.getName());
            redirectAttributes.addFlashAttribute("successMsg", "Shared task rejected!");
        } catch (BaseException ex) {
            redirectAttributes.addFlashAttribute("errorMsg", getLocalizedMessage(ex, locale));
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMsg", "An error occurred while rejecting the task.");
        }
        return "redirect:/friends";
    }

}