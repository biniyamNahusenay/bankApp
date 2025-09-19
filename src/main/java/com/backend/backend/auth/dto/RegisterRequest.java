package com.backend.backend.auth.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String phone;
    private String email; // optional
}
