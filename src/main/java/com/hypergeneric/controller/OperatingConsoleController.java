package com.hypergeneric.controller;

import com.hypergeneric.model.DatabaseConfig;
import com.hypergeneric.service.DatabaseConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller
@RequestMapping("/operatingconsole")
public class OperatingConsoleController {
    
    private static final Logger logger = LoggerFactory.getLogger(OperatingConsoleController.class);
    
    @Autowired
    private DatabaseConfigService databaseConfigService;

    @GetMapping("/login")
    public String login() {
        logger.debug("OperatingConsoleController.login() method called");
        logger.info("Attempting to render login view");
        return "login";
    }

    @GetMapping("/")
    public String index(Model model) {
        logger.debug("OperatingConsoleController.index() method called");
        logger.info("Loading operating console index page");
        
        // Get authenticated user information
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        
        // Get all datasource configurations
        try {
            List<DatabaseConfig> datasources = databaseConfigService.getAllConfigs();
            model.addAttribute("datasources", datasources);
            logger.info("Loaded {} datasource configurations", datasources.size());
        } catch (Exception e) {
            logger.error("Error loading datasource configurations: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Failed to load datasource configurations: " + e.getMessage());
        }
        
        return "index";
    }
}