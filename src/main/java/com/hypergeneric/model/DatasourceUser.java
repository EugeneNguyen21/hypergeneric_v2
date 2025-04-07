package com.hypergeneric.model;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "datasource_users")
public class DatasourceUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    private String datasourceName;
} 