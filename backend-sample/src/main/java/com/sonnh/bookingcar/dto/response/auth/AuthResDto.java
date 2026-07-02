package com.sonnh.bookingcar.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResDto {
    private String accessToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private UUID userId;
    private String username;
    private String role;
}
