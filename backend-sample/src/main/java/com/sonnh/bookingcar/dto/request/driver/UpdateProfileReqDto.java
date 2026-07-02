package com.sonnh.bookingcar.dto.request.driver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileReqDto {
    private String fullName;
    private String email;
    private String profileImage;
}
