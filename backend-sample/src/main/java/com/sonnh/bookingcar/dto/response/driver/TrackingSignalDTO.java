package com.sonnh.bookingcar.dto.response.driver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackingSignalDTO {
    private boolean trackingRequired;
}
