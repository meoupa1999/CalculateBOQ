package com.sonnh.bookingcar.dto.request.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalTime;

@Data
public class AdminUpdateTourTimeReqDto {
    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(type = "string", example = "12:00:00", description = "Format: HH:mm:ss")
    private LocalTime time;
}
