package com.hypergeneric.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/operatingconsole")
public class OperatingConsoleController {

    @GetMapping("/login")
    public String login() {
        return "operatingconsole/login";
    }

    @GetMapping("/")
    public String index() {
        return "operatingconsole/index";
    }
} 