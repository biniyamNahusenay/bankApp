package com.backend.backend.otp;
import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "activation_code")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ActivationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(length = 255)
    private String email; // optional, captured at registration

    @Column(nullable = false, length = 6)
    private String code; // 6-digit string

    @Column(nullable = false)
    private OffsetDateTime expiresAt;

    @Column(nullable = false)
    private boolean consumed;

    @Column(nullable = false)
    private int attempts;
}