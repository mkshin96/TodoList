package me.mugon.todolist.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping
    public String login() {
        return "/login";
    }

    @GetMapping("/success")
    public String loginSuccess() {
        return "redirect:/todolists";
    }

    @GetMapping("/oauth/success")
    public String oauth2LoginSuccess() {
        return "redirect:/todolists/oauth";
    }
}
