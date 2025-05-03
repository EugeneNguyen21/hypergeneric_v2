package com.hypergeneric.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/test")
    @ResponseBody
    public String test(HttpServletRequest request) {
        logger.info("Test controller accessed from IP: {}", request.getRemoteAddr());
        return "Test controller is working!";
    }

    @GetMapping("/test/login")
    @ResponseBody
    public String testLogin() {
        logger.info("Test login endpoint accessed");
        return "Test login endpoint is working!";
    }

    @GetMapping("/operatingconsole/test")
    @ResponseBody
    public String testOperatingConsole() {
        logger.info("Operating console test endpoint accessed");
        return "Operating console test endpoint is working!";
    }
}