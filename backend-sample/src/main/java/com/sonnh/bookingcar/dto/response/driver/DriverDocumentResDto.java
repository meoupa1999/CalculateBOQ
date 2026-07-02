package com.sonnh.bookingcar.dto.response.driver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverDocumentResDto {
    private UUID id;
    private String documentType;
    private String documentNumber;
    private LocalDate issuedDate;
    private LocalDate expiredDate;
    private String status;
    private List<String> imageUrls;
}
