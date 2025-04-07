package com.hypergeneric.controller;

import com.hypergeneric.model.DatabaseConfig;
import com.hypergeneric.service.DatabaseConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        return "datasource-login";
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