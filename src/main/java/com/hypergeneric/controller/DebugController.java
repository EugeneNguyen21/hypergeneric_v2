package com.hypergeneric.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/debug")
public class DebugController {

    private static final Logger logger = LoggerFactory.getLogger(DebugController.class);
    
    @Autowired
    private ResourceLoader resourceLoader;
    
    @GetMapping("/templates")
    @ResponseBody
    public String listTemplates() {
        StringBuilder result = new StringBuilder();
        result.append("<h2>Debug Template Resources</h2>");
        
        result.append("<h3>Checking key template files:</h3>");
        result.append("<pre>");
        
        // Check specific templates we care about
        List<String> templatePaths = Arrays.asList(
            "classpath:/templates/operatingconsole/login.html",
            "classpath:/templates/operatingconsole/index.html",
            "classpath:/templates/datasource/datasource_login.html",
            "classpath:/templates/error_view.html"
        );
        
        for (String path : templatePaths) {
            Resource resource = resourceLoader.getResource(path);
            result.append(String.format("Template: %-50s Exists: %s\n", path, resource.exists()));
            
            if (resource.exists()) {
                try {
                    result.append(String.format("  - File path: %s\n", resource.getFile().getAbsolutePath()));
                    result.append(String.format("  - File size: %d bytes\n", resource.contentLength()));
                } catch (IOException e) {
                    result.append("  - Error getting details: " + e.getMessage() + "\n");
                }
            }
        }
        
        result.append("</pre>");
        
        result.append("<h3>Application Status:</h3>");
        result.append("<pre>");
        result.append("Current Working Directory: " + System.getProperty("user.dir") + "\n");
        result.append("Java Version: " + System.getProperty("java.version") + "\n");
        result.append("</pre>");
        
        return result.toString();
    }
}