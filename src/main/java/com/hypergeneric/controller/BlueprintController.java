package com.hypergeneric.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for managing blueprint operations.
 * Handles blueprint creation, editing, and viewing.
 */
@Controller
@RequestMapping("/blueprint")
public class BlueprintController {

    /**
     * Displays the blueprint builder interface.
     * 
     * @return The blueprint builder view
     */
    @GetMapping("/builder")
    public String showBlueprintBuilder() {
        return "datasource/blueprint_builder";
    }
}