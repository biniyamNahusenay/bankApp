package com.backend.backend.auth.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String phone;
    private String pin;
}
