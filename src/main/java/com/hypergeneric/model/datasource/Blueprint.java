package com.hypergeneric.model.datasource;

import lombok.Data;
import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "blueprint")
public class Blueprint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "active_version_id")
    private Integer activeVersionId;
    
    @OneToMany(mappedBy = "blueprint", cascade = CascadeType.ALL)
    private List<BlueprintVersion> versions;
    
    @OneToMany(mappedBy = "blueprint", cascade = CascadeType.ALL)
    private List<Item> items;
    
    @OneToMany(mappedBy = "blueprint", cascade = CascadeType.ALL)
    private List<Field> fields;
}