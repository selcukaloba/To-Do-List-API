package com.selcukaloba.to_do_api_project.controller.web;

import com.selcukaloba.to_do_api_project.dto.ApiCommentResponse;
import com.selcukaloba.to_do_api_project.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class CommentController {

    @Autowired
    private ICommentService commentService;

    @GetMapping("/dashboard/todo/{todoId}/comments")
    public String showCommentsPage(@PathVariable Long todoId, Model model, Principal principal) {
        List<ApiCommentResponse> comments = commentService.getComments(todoId);
        model.addAttribute("comments", comments);
        model.addAttribute("todoId", todoId);
        model.addAttribute("username", principal.getName());
        return "comments";
    }

    @PostMapping("/dashboard/todo/{todoId}/comment")
    public String addComment(@PathVariable Long todoId,
                             @RequestParam String content,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        commentService.addComment(todoId, principal.getName(), content);
        redirectAttributes.addFlashAttribute("successMsg", "Comment added!");
        return "redirect:/dashboard/todo/" + todoId + "/comments";
    }
}