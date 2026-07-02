package com.sonnh.bookingcar.dto.request.tourist;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TouristRegisterReqDto {
    @NotBlank
    private String password;

    @NotBlank
    private String phone;

    private String fullname;

    @Email
    private String mail;
}
