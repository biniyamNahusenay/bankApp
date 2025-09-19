package com.backend.backend.auth;

import com.backend.backend.auth.dto.*;
import com.backend.backend.security.JwtService;
import com.backend.backend.otp.OtpService;
import com.backend.backend.otp.ActivationCodeRepository;
import com.backend.backend.sms.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OtpService otpService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ActivationCodeRepository activationCodeRepository;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            return ResponseEntity.badRequest().body("Phone already registered");
        }
        if (request.getEmail() != null && !request.getEmail().isBlank() && userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        String code = otpService.generateActivationCode(request.getPhone(), request.getEmail());
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            emailService.send(request.getEmail(), "Your Activation Code", "Your activation code is: " + code + " (valid 10 minutes)");
        }
        return ResponseEntity.ok("Activation code sent (SMS simulated, email if provided)");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequest request) {
        boolean valid = otpService.verify(request.getPhone(), request.getCode());
        if (!valid) return ResponseEntity.badRequest().body("Invalid or expired code");

        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            return ResponseEntity.badRequest().body("Phone already registered");
        }

        var acOpt = activationCodeRepository.findTopByPhoneNumberOrderByIdDesc(request.getPhone());
        String email = acOpt.flatMap(ac -> java.util.Optional.ofNullable(ac.getEmail())).orElse(null);
        if (email != null && userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        var user = new User(request.getPhone());
        if (email != null && !email.isBlank()) {
            user.setEmail(email);
        }
        user.setActive(true);
        userRepository.save(user);

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            emailService.send(user.getEmail(), "Your PIN", "Your temporary PIN is: " + user.getPin() + "\nKeep it secure and change it after login.");
        }

        return ResponseEntity.ok("Phone verified successfully. Now you can login using your PIN.");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        var user = userRepository.findByPhone(request.getPhone()).orElseThrow();

        if (!user.isActive()) return ResponseEntity.status(403).body(null);
        if (!user.getPin().equals(request.getPin())) return ResponseEntity.status(401).body(null);

        String token = jwtService.generateToken(user.getPhone());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
