package com.safeg.user.vo;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "phone_auth")
public class PhoneAuth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phoneNumber;
    private String authCode;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean used;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        used = false;
    }
    
    // Getters, Setters...
}