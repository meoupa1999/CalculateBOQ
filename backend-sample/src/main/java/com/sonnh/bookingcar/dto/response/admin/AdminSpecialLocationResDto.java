package com.sonnh.bookingcar.dto.response.admin;

import lombok.Data;
import java.util.UUID;

@Data
public class AdminSpecialLocationResDto {
    private UUID id;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private Boolean isActive;
}
