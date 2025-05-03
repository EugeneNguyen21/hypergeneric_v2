package com.hypergeneric.model.datasource;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "blueprint_id")
    private Integer blueprintId;
    
    @ManyToOne
    @JoinColumn(name = "blueprint_id", insertable = false, updatable = false)
    private Blueprint blueprint;
    
    @Column(name = "version_id")
    private Integer versionId;
    
    @Column(name = "current_state")
    private String currentState;
    
    @Column(name = "data_json", columnDefinition = "json")
    private String dataJson;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @OneToMany(mappedBy = "fromItem")
    private List<ItemLink> outgoingLinks;
    
    @OneToMany(mappedBy = "toItem")
    private List<ItemLink> incomingLinks;
}