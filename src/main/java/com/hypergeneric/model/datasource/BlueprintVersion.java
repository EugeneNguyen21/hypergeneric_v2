package com.hypergeneric.model.datasource;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "blueprint_version")
public class BlueprintVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "blueprint_id")
    private Integer blueprintId;
    
    @ManyToOne
    @JoinColumn(name = "blueprint_id", insertable = false, updatable = false)
    private Blueprint blueprint;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "label")
    private String label;
    
    @Column(name = "data_json", columnDefinition = "json")
    private String dataJson;
}