package com.selcukaloba.to_do_api_project.controller;

import com.selcukaloba.to_do_api_project.dto.auth.ApiAuthRequest;
import com.selcukaloba.to_do_api_project.dto.auth.ApiAuthResponse;
import com.selcukaloba.to_do_api_project.service.IAuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {

    @GetMapping("/login")
    public String loginPage()
    {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {return "auth/register";}

    @GetMapping("/dashboard")
    public String dashboardPage() {return "todo/dashboard";}

    @GetMapping("/friends")
    public String friendsPage() {return "friend/friends";}
}
