package com.sonnh.bookingcar.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginReqDto {
    @NotBlank
    private String username; // Can be username or phone

    @NotBlank
    private String password;
}
