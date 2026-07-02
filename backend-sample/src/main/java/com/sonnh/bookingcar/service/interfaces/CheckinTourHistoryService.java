package com.sonnh.bookingcar.service.interfaces;

import com.sonnh.bookingcar.dto.response.admin.CheckinTourHistoryResDto;
import java.util.List;
import java.util.UUID;

public interface CheckinTourHistoryService {
    List<CheckinTourHistoryResDto> getAll(UUID tourId);
    CheckinTourHistoryResDto getById(UUID id);
}
