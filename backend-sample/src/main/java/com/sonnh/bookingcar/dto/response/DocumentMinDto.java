package com.sonnh.bookingcar.dto.response;

import com.sonnh.bookingcar.data.domain.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentMinDto {
    private UUID documentId;
    private DocumentType documentType;
}
