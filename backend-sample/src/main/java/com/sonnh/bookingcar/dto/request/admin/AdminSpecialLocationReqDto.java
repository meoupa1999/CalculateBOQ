package com.sonnh.bookingcar.dto.request.admin;

import lombok.Data;

@Data
public class AdminSpecialLocationReqDto {
    private String locationName;
    private Double latitude;
    private Double longitude;
}
