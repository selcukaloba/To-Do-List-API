package com.selcukaloba.to_do_api_project.controller.web;

import com.selcukaloba.to_do_api_project.dto.ApiCommentResponse;
import com.selcukaloba.to_do_api_project.util.IdEncoder;
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

    @GetMapping("/dashboard/todo/{encodedId}/comments")
    public String showCommentsPage(@PathVariable String encodedId, Model model, Principal principal) {
        Long todoId = IdEncoder.decode(encodedId);
        List<ApiCommentResponse> comments = commentService.getComments(todoId, principal.getName());
        model.addAttribute("comments", comments);
        model.addAttribute("todoId", todoId);
        model.addAttribute("encodedId", encodedId);
        model.addAttribute("username", principal.getName());
        return "comments";
    }

    @PostMapping("/dashboard/todo/{encodedId}/comment")
    public String addComment(@PathVariable String encodedId,
                             @RequestParam String content,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        Long todoId = IdEncoder.decode(encodedId);
        commentService.addComment(todoId, principal.getName(), content);
        redirectAttributes.addFlashAttribute("successMsg", "Comment added!");
        return "redirect:/dashboard/todo/" + encodedId + "/comments";
    }
}