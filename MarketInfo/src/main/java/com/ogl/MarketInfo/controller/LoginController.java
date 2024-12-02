package com.ogl.MarketInfo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login() {
        return "/login/login";
    }
    @GetMapping("/registro")
    public String registro() {
        return "/login/registro";
    }
}
