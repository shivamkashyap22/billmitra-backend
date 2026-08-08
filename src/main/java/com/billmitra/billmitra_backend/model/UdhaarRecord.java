package com.billmitra.billmitra_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "udhaar_records")
@Data
public class UdhaarRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Double amount;
    private String status = "PENDING";
    private String note;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}