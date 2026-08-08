package com.billmitra.billmitra_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "shops")
@Data
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String ownerName;
    private String phoneNumber;
    private String city;
    private String state;
    private boolean isActive = true;

    @Version
    private Long version;

    @Column(updatable = false)
    private LocalDateTime createAt = LocalDateTime.now();
}