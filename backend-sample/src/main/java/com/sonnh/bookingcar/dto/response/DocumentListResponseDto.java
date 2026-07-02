package com.sonnh.bookingcar.dto.response;

import com.sonnh.bookingcar.data.domain.enums.DocumentOwnerType;
import com.sonnh.bookingcar.data.domain.enums.DocumentStatus;
import com.sonnh.bookingcar.data.domain.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentListResponseDto {
    private UUID id;
    private DocumentOwnerType ownerType;
    private DocumentType documentType;
    private LocalDate expiryDate;
    private DocumentStatus status;
    private LocalDateTime updatedAt;
    
    private DriverInfo driver;
    private VehicleInfo vehicle;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DriverInfo {
        private UUID id;
        private String fullName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleInfo {
        private UUID id;
        private String plateNumber;
    }
}
