package com.hypergeneric.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigSection {
    private String name;
    private String id;
    private List<ConfigCategory> categories = new ArrayList<>();
    
    public ConfigSection(String name, String id) {
        this.name = name;
        this.id = id;
    }
    
    public ConfigSection addCategory(ConfigCategory category) {
        this.categories.add(category);
        return this;
    }
}