package com.hypergeneric.controller;

import com.hypergeneric.model.DatasourceUser;
import com.hypergeneric.service.DatasourceUserService;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/hypergeneric")
@RequiredArgsConstructor
public class DatasourceAuthController {
    private final DatasourceUserService userService;

    @GetMapping("/{datasourceName}")
    public String handleDatasourcePage(@PathVariable String datasourceName,
                                     HttpSession session,
                                     Model model) {
        if (!isAuthenticated(session)) {
            model.addAttribute("datasourceName", datasourceName);
            return "datasource_login";
        }
        
        model.addAttribute("datasourceName", datasourceName);
        model.addAttribute("tabs", new String[]{"dashboard", "navigator", "search", "creation", "utilities"});
        model.addAttribute("activeTab", session.getAttribute("activeTab") != null ? 
                                      session.getAttribute("activeTab") : "dashboard");
        return "datasource_tabbed_view";
    }

    @PostMapping("/{datasourceName}/login")
    public String login(@PathVariable String datasourceName,
                       @RequestParam String username,
                       @RequestParam String password,
                       HttpSession session,
                       Model model) {
        return userService.authenticateUserByName(username, password, datasourceName)
                .map(user -> {
                    session.setAttribute("user_id", user.getId());
                    session.setAttribute("datasource_name", datasourceName);
                    return "redirect:/hypergeneric/" + datasourceName;
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "Invalid username or password");
                    model.addAttribute("datasourceName", datasourceName);
                    return "datasource_login";
                });
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
        return session.getAttribute("user_id") != null &&
               session.getAttribute("datasource_name") != null;
    }
} 