package com.sonnh.elv.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigResponseDto {
    private UUID id;
    private Integer conditionLength;
    private Integer sw24ConditionQuanity;
    private Integer sw16ConditionQuanity;
    private Integer ups;
    private Integer pdu;
    private Integer converter;
}
