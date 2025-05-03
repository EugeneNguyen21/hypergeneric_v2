package com.hypergeneric.model.datasource;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "item_link")
public class ItemLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "from_item_id")
    private Integer fromItemId;
    
    @ManyToOne
    @JoinColumn(name = "from_item_id", insertable = false, updatable = false)
    private Item fromItem;
    
    @Column(name = "to_item_id")
    private Integer toItemId;
    
    @ManyToOne
    @JoinColumn(name = "to_item_id", insertable = false, updatable = false)
    private Item toItem;
    
    @Column(name = "field_id")
    private Integer fieldId;
    
    @ManyToOne
    @JoinColumn(name = "field_id", insertable = false, updatable = false)
    private Field field;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "created_by")
    private String createdBy;
}