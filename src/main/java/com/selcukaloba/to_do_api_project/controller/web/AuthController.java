package com.selcukaloba.to_do_api_project.controller.web;

import com.selcukaloba.to_do_api_project.dto.ApiRegisterRequest;
import com.selcukaloba.to_do_api_project.service.IAuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private IAuthService authService;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute ApiRegisterRequest registerRequest, Model model) {
        try {
            authService.register(registerRequest);
            return "redirect:/login?registered=true";
        } catch (Exception ex) {
            model.addAttribute("errorMsg", ex.getMessage());
            return "register";
        }
    }
}
