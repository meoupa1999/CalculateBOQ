package com.sonnh.bookingcar.dto;

import com.sonnh.bookingcar.data.domain.embedded.Audit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTouristDto {
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String profileImage;
    private Audit audit;
}
