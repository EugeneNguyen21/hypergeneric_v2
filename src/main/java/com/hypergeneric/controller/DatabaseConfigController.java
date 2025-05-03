package com.hypergeneric.controller;

import com.hypergeneric.model.DatabaseConfig;
import com.hypergeneric.service.DatabaseConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/operatingconsole/datasource")
@RequiredArgsConstructor
public class DatabaseConfigController {
    private final DatabaseConfigService service;

    @GetMapping("/list")
    @ResponseBody
    public List<DatabaseConfig> getAllConfigs() {
        return service.getAllConfigs();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public DatabaseConfig getConfig(@PathVariable Long id) {
        return service.getConfigById(id);
    }

    @PutMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<?> updateConfig(@PathVariable Long id, @RequestBody DatabaseConfig config) {
        try {
            config.setId(id);
            service.saveConfig(config);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating configuration: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/login")
    public String accessDatasource(@PathVariable Long id, Model model) {
        DatabaseConfig config = service.getConfigById(id);
        model.addAttribute("config", config);
        return "datasource/datasource_login";
    }

    @PostMapping("/{id}/connect")
    public String connectToDatasource(@PathVariable Long id, 
                                     @RequestParam String username,
                                     @RequestParam String password,
                                     HttpSession session,
                                     Model model) {
        try {
            DatabaseConfig config = service.getConfigById(id);
            
            // Validate credentials against the datasource's users table
            boolean authenticated = service.authenticateAgainstDatasource(config, username, password);
            
            if (authenticated) {
                // Store connection details in session only if authentication succeeded
                session.setAttribute("datasource_id", id);
                session.setAttribute("datasource_name", config.getDsName());
                session.setAttribute("datasource_username", username);
                
                // Redirect to the hypergeneric interface
                return "redirect:/hypergeneric/" + config.getDsName();
            } else {
                model.addAttribute("error", "Invalid username or password for this datasource");
                model.addAttribute("config", config);
                return "datasource/datasource_login";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Failed to connect: " + e.getMessage());
            model.addAttribute("config", service.getConfigById(id));
            return "datasource/datasource_login";
        }
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("config", new DatabaseConfig());
        return "add_datasource";
    }

    @PostMapping("/test-connection")
    @ResponseBody
    public ResponseEntity<Map<String, String>> testConnection(@RequestBody DatabaseConfig config) {
        try {
            boolean success = service.testConnection(config);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Connection successful!"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<Map<String, String>> saveConfig(@RequestBody DatabaseConfig config) {
        try {
            service.saveConfig(config);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Database configuration saved successfully!"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "status", "error",
                "message", "Failed to save database configuration: " + e.getMessage()
            ));
        }
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteConfig(@PathVariable Long id) {
        try {
            service.deleteConfig(id);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Database configuration deleted successfully!"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "status", "error",
                "message", "Failed to delete database configuration: " + e.getMessage()
            ));
        }
    }
}