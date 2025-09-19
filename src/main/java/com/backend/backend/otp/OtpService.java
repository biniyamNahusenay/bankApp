package com.backend.backend.otp;

import com.backend.backend.sms.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class OtpService {
   private final ActivationCodeRepository repo;
   private final SmsService sms;
   private final SecureRandom random = new SecureRandom();

    public String generateActivationCode(String phone, String email) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        ActivationCode ac = ActivationCode.builder()
                .phoneNumber(phone)
                .email(email)
                .code(code)
                .expiresAt(OffsetDateTime.now().plusMinutes(10))
                .consumed(false)
                .attempts(0)
                .build();
        repo.save(ac);
        sms.send(phone, "Your activation code is: " + code + " (valid 10 minutes)");
        return code;
    }
    public boolean verify(String phone, String code) {
        var acOpt = repo.findTopByPhoneNumberAndConsumedIsFalseOrderByIdDesc(phone);
        if (acOpt.isEmpty()) return false;
        var ac = acOpt.get();
        if (ac.getExpiresAt().isBefore(OffsetDateTime.now())) return false;
        ac.setAttempts(ac.getAttempts()+1);
        boolean ok = ac.getCode().equals(code);
        if (ok) ac.setConsumed(true);
        repo.save(ac);
        return ok;
    }
}
