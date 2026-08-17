package com.selcukaloba.to_do_api_project.controller.web;

import com.selcukaloba.to_do_api_project.dto.ApiFriendRequestResponse;
import com.selcukaloba.to_do_api_project.dto.ApiUserResponse;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoResponse;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoShareRequestResponse;
import com.selcukaloba.to_do_api_project.service.IFriendRequestService;
import com.selcukaloba.to_do_api_project.service.ITodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Controller
public class FriendsController {

    @Autowired
    private IFriendRequestService friendRequestService;

    @Autowired
    private ITodoService todoService;

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
    public String unfriend(@PathVariable String friendUsername,
                           @RequestParam(defaultValue = "false") boolean keepShared,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        friendRequestService.unfriend(principal.getName(), friendUsername, keepShared);
        redirectAttributes.addFlashAttribute("successMsg", "Unfriended successfully!");
        return "redirect:/friends";
    }
}
