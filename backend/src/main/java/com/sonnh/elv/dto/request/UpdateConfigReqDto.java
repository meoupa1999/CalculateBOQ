package com.sonnh.elv.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConfigReqDto {
    private Integer conditionLength;
    private Integer sw24ConditionQuanity;
    private Integer sw16ConditionQuanity;
    private Integer ups;
    private Integer pdu;
    private Integer converter;
}
