package com.sonnh.bookingcar.dto.request.driver;

import com.sonnh.bookingcar.data.domain.enums.CheckinStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverCheckinReqDto {
    private UUID tourId;
    private UUID specialLocationTourId;
    private CheckinStatus status;
    private String reasonNote;
}
