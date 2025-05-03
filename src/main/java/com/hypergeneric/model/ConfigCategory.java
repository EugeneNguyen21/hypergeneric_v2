package com.hypergeneric.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigCategory {
    private String name;
    private String icon;
    private String id;
    private String additionalClass = "";
    private List<ConfigItem> items = new ArrayList<>();
    
    public ConfigCategory(String name, String icon, String id) {
        this.name = name;
        this.icon = icon;
        this.id = id;
    }
    
    public ConfigCategory(String name, String icon, String id, String additionalClass) {
        this.name = name;
        this.icon = icon;
        this.id = id;
        this.additionalClass = additionalClass;
    }
    
    public ConfigCategory addItem(String icon, String name) {
        this.items.add(new ConfigItem(icon, name));
        return this;
    }
}