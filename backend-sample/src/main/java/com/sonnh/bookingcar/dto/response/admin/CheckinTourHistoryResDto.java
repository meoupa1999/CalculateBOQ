package com.sonnh.bookingcar.dto.response.admin;

import com.sonnh.bookingcar.data.domain.enums.CheckinStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckinTourHistoryResDto {
    private UUID id;
    private CheckinStatus status;
    private String reasonNote;
    private LocalDateTime createdAt;
    
    // Associations info
    private UUID driverId;
    private String driverName;
    
    private UUID tourId;
    private String tourName;
    
    private UUID specialLocationId;
    private String locationName;
    private Integer priority;
}
