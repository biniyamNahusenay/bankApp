package com.backend.backend.sms;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsService {
    public void send(String phone, String message) {
// DEV ONLY: simulate SMS via logs
        log.info("[SMS -> {}] {}", phone, message);
    }
}
