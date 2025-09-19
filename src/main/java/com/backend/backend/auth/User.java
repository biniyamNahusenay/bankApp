package com.backend.backend.auth;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String phone;

    @Column(unique = true)
    private String email; // optional

    @Column(nullable = false)
    private String pin;

    private boolean active = false;

    public User(String phone) {
        this.phone = phone;
        this.pin = generateRandomPin();
        this.active = false;
    }

    private String generateRandomPin() {
        int number = 1000 + (int)(Math.random() * 9000); // 4-digit PIN
        return String.valueOf(number);
    }
}
