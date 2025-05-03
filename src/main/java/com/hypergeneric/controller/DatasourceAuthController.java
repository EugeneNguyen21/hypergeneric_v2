package com.hypergeneric.controller;

import com.hypergeneric.model.DatasourceUser;
import com.hypergeneric.model.DatabaseConfig;
import com.hypergeneric.service.DatasourceUserService;
import com.hypergeneric.service.DatabaseConfigService;
import com.hypergeneric.service.ConfigurationService;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

@Controller
@RequestMapping("/hypergeneric")
@RequiredArgsConstructor
public class DatasourceAuthController {
    private final DatasourceUserService userService;
    private final DatabaseConfigService databaseConfigService;
    private final ConfigurationService configurationService;
    private static final Logger logger = LoggerFactory.getLogger(DatasourceAuthController.class);

    @GetMapping("/{datasourceName}")
    public String handleDatasourcePage(@PathVariable String datasourceName,
                                     HttpSession session,
                                     Model model) {
        // First check if the datasource exists
        try {
            // Check if datasource exists by name
            Optional<DatabaseConfig> datasourceConfig = databaseConfigService.findByDsName(datasourceName);
            
            if (datasourceConfig.isEmpty()) {
                logger.error("Datasource not found: {}", datasourceName);
                model.addAttribute("error", "Datasource '" + datasourceName + "' does not exist");
                model.addAttribute("returnUrl", "/operatingconsole/");
                return "error_view";
            }
            
            // Continue with normal flow if datasource exists
            if (!isAuthenticated(session)) {
                model.addAttribute("datasourceName", datasourceName);
                return "datasource/datasource_login";
            }
            
            model.addAttribute("datasourceName", datasourceName);
            model.addAttribute("tabs", configurationService.getAvailableTabs());
            model.addAttribute("activeTab", session.getAttribute("activeTab") != null ? 
                                          session.getAttribute("activeTab") : "dashboard");
            
            // Add configuration data if the active tab is utilities
            String activeTab = (String) (session.getAttribute("activeTab") != null ? 
                                       session.getAttribute("activeTab") : "dashboard");
            if ("utilities".equals(activeTab)) {
                model.addAttribute("configSections", configurationService.getUtilitiesConfiguration());
            }
            
            return "datasource/datasource_tabbed_view";
        } catch (Exception e) {
            logger.error("Error accessing datasource {}: {}", datasourceName, e.getMessage(), e);
            model.addAttribute("error", "Error accessing datasource: " + e.getMessage());
            model.addAttribute("returnUrl", "/operatingconsole/");
            return "error_view";
        }
    }

    @PostMapping("/{datasourceName}/login")
    public String login(@PathVariable String datasourceName,
                       @RequestParam String username,
                       @RequestParam String password,
                       HttpSession session,
                       Model model) {
        // First verify datasource exists
        try {
            Optional<DatabaseConfig> datasourceConfigOpt = databaseConfigService.findByDsName(datasourceName);
            if (datasourceConfigOpt.isEmpty()) {
                model.addAttribute("error", "Datasource '" + datasourceName + "' does not exist");
                model.addAttribute("returnUrl", "/operatingconsole/");
                return "error_view";
            }
            
            DatabaseConfig datasourceConfig = datasourceConfigOpt.get();
            
            // Authenticate against the actual datasource's users table
            boolean authenticated = databaseConfigService.authenticateAgainstDatasource(
                datasourceConfig, username, password);
            
            if (authenticated) {
                // Store authentication information in session
                session.setAttribute("datasource_id", datasourceConfig.getId());
                session.setAttribute("datasource_name", datasourceName);
                session.setAttribute("datasource_username", username);
                return "redirect:/hypergeneric/" + datasourceName;
            } else {
                model.addAttribute("error", "Invalid username or password");
                model.addAttribute("datasourceName", datasourceName);
                return "datasource/datasource_login";
            }
        } catch (Exception e) {
            logger.error("Error during login to datasource {}: {}", datasourceName, e.getMessage(), e);
            model.addAttribute("error", "Login error: " + e.getMessage());
            model.addAttribute("returnUrl", "/operatingconsole/");
            return "error_view";
        }
    }

    @GetMapping("/{datasourceName}/tab/{tabName}")
    public String switchTab(@PathVariable String datasourceName,
                          @PathVariable String tabName,
                          HttpSession session,
                          Model model) {
        if (!isAuthenticated(session)) {
            return "redirect:/hypergeneric/" + datasourceName;
        }
        
        session.setAttribute("activeTab", tabName);
        return "redirect:/hypergeneric/" + datasourceName;
    }

    @GetMapping("/{datasourceName}/logout")
    public String logout(@PathVariable String datasourceName, HttpSession session) {
        session.invalidate();
        return "redirect:/hypergeneric/" + datasourceName;
    }

    private boolean isAuthenticated(HttpSession session) {
        // Check if either the old authentication method or the new one is present
        boolean hasOldAuth = session.getAttribute("user_id") != null && 
                            session.getAttribute("datasource_name") != null;
        
        boolean hasNewAuth = session.getAttribute("datasource_id") != null && 
                            session.getAttribute("datasource_name") != null && 
                            session.getAttribute("datasource_username") != null;
        
        return hasOldAuth || hasNewAuth;
    }
}