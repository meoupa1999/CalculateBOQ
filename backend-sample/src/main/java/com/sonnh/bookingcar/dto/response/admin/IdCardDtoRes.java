package com.sonnh.bookingcar.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdCardDtoRes {
    private String placeOfBirth;
    private String placeOfResidence;
}
