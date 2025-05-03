package com.hypergeneric.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigItem {
    private String name;
    private String icon;
    private String action;
    
    // Constructor for simpler creation
    public ConfigItem(String icon, String name) {
        this.icon = icon;
        this.name = name;
        this.action = "";
    }
}