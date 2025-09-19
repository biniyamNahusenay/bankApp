package com.backend.backend.otp;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivationCodeRepository extends JpaRepository<ActivationCode,Long> {
    Optional<ActivationCode> findTopByPhoneNumberAndConsumedIsFalseOrderByIdDesc(String phoneNumber);
    Optional<ActivationCode> findTopByPhoneNumberOrderByIdDesc(String phoneNumber);
}
